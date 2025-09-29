package com.sobow.shopping.services.order.Impl;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.order.OrderItem;
import com.sobow.shopping.domain.order.OrderRepository;
import com.sobow.shopping.domain.order.OrderStatus;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.exceptions.CartEmptyException;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.services.cart.CartService;
import com.sobow.shopping.services.order.OrderService;
import com.sobow.shopping.services.product.ProductService;
import com.sobow.shopping.services.user.CurrentUserService;
import com.sobow.shopping.services.user.UserProfileService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final OrderRepository orderRepository;
    private final UserProfileService userProfileService;
    private final ProductService productService;
    private final CartService cartService;
    private final CurrentUserService currentUserService;
    
    @Transactional
    @Override
    public Order selfCreateOrder() {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        
        // Load UserProfile and Cart with items
        UserProfile userProfile = userProfileService.findByUserId(user.getId());
        Cart cart = cartService.findByUserIdWithItems(user.getId());
        
        // Assert cart is not empty
        if (cart.getCartItems().isEmpty()) {
            throw new CartEmptyException(cart.getId());
        }
        
        // Lock + assert stock available + decrement
        productService.lockForOrder(cart.getProductsId());
        assertStockAvailableAndDecrement(cart.getCartItems());
        
        // Build order from cart
        Order order = orderFrom(cart);
        
        // Link to user
        userProfile.addOrderAndLink(order);
        
        // Remove cart after successful order creation
        cart.removeAllCartItems();
        entityManager.flush();
        userProfile.removeCart();
        
        return order;
    }
    
    @Override
    public Order selfFindByIdWithItems(long orderId) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        
        return orderRepository.findByUserIdAndIdWithOrderItems(user.getId(), orderId)
                              .orElseThrow(
                                  () -> new EntityNotFoundException(
                                      "Order with id " + orderId + " for user " + user.getId() + " not found")
                              );
    }
    
    @Override
    public List<Order> selfFindAllWithItems() {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        
        return orderRepository.findAllByUserIdWithOrderItems(user.getId());
    }
    
    @Override
    public Order findByUserIdAndIdWithItems(long userId, long orderId) {
        return orderRepository.findByUserIdAndIdWithOrderItems(userId, orderId)
                              .orElseThrow(
                                  () -> new EntityNotFoundException(
                                      "Order with id " + orderId + " for user " + userId + " not found")
                              );
    }
    
    @Override
    public List<Order> findAllByUserIdWithItems(long userId) {
        return orderRepository.findAllByUserIdWithOrderItems(userId);
    }
    
    private void assertStockAvailableAndDecrement(Set<CartItem> items) {
        for (CartItem item : items) {
            // Assert products still available
            Product product = productService.findById(item.getProduct().getId());
            int availableQty = product.getAvailableQty();
            int requestedQty = item.getRequestedQty();
            if (requestedQty > availableQty) {
                throw new InsufficientStockException(product.getId(), availableQty, requestedQty);
            }
            
            // Decrement stock
            int newQty = availableQty - requestedQty;
            product.setAvailableQty(newQty);
        }
    }
    
    private Order orderFrom(Cart cart) {
        Order order = new Order(OrderStatus.NEW);
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = orderItemFrom(cartItem);
            order.addItemAndLink(orderItem);
        }
        return order;
    }
    
    private OrderItem orderItemFrom(CartItem cartItem) {
        return OrderItem.builder()
                        .requestedQty(cartItem.getRequestedQty())
                        .productName(cartItem.getProduct().getName())
                        .productBrandName(cartItem.getProduct().getBrandName())
                        .productPrice(cartItem.getProductPrice())
                        .build();
    }
}
