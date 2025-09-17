package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.entities.Cart;
import com.sobow.shopping.domain.entities.CartItem;
import com.sobow.shopping.domain.entities.Order;
import com.sobow.shopping.domain.entities.OrderItem;
import com.sobow.shopping.domain.entities.OrderStatus;
import com.sobow.shopping.domain.entities.Product;
import com.sobow.shopping.domain.entities.UserProfile;
import com.sobow.shopping.exceptions.CartEmptyException;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.repositories.OrderRepository;
import com.sobow.shopping.services.CartService;
import com.sobow.shopping.services.OrderService;
import com.sobow.shopping.services.ProductService;
import com.sobow.shopping.services.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final UserProfileService userProfileService;
    private final ProductService productService;
    private final CartService cartService;
    
    @Transactional
    @Override
    public Order createOrder(long userId) {
        // Load cart with items
        UserProfile userProfile = userProfileService.findByUserId(userId);
        Cart cart = cartService.findByIdWithItems(userProfile.getCart().getId());
        
        // Assert cart is not empty
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new CartEmptyException(Optional.of(cart));
        }
        
        // Lock + assert stock available + decrement
        productService.lockForOrder(cart.getProductsId());
        assertStockAvailableAndDecrementQty(cart.getCartItems());
        
        // Build order from cart
        Order order = orderFrom(cart);
        
        // Link to user
        userProfile.addOrderAndLink(order);
        
        // Clear cart after successful order creation
        userProfile.removeCartAndUnlink();
        
        return order;
    }
    
    @Override
    public Order findById(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
            () -> new EntityNotFoundException("Order with id " + orderId + " not found"));
    }
    
    private void assertStockAvailableAndDecrementQty(Set<CartItem> items) {
        for (CartItem item : items) {
            Product p = item.getProduct();
            int availableQty = p.getAvailableQty();
            int requestedQty = item.getRequestedQty();
            if (requestedQty > availableQty) {
                throw new InsufficientStockException(p.getId(), availableQty, requestedQty);
            }
            
            productService.decrementAvailableQty(p.getId(), item.getRequestedQty());
        }
    }
    
    private Order orderFrom(Cart cart) {
        Order order = new Order();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = orderItemFrom(cartItem);
            order.addItemAndLink(orderItem);
        }
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(cart.getTotalPrice());
        return order;
    }
    
    private OrderItem orderItemFrom(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setRequestedQty(cartItem.getRequestedQty());
        orderItem.setProductPrice(cartItem.unitPrice());
        orderItem.setProductName(cartItem.getProduct().getName());
        orderItem.setProductBrandName(cartItem.getProduct().getBrandName());
        return orderItem;
    }
}
