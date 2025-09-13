package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.exceptions.OutOfStockException;
import com.sobow.shopping.repositories.CartItemRepository;
import com.sobow.shopping.repositories.CartRepository;
import com.sobow.shopping.services.CartService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    
    @Override
    public Cart createCartForUser(Long userId) {
        throw new UnsupportedOperationException("createCartForUser is not implemented yet");
    }
    
    @Override
    public void removeCartForUser(Long userId) {
        throw new UnsupportedOperationException("removeCartForUser is not implemented yet");
    }
    
    @Override
    public Cart findCartById(Long id) {
        return cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart with " + id + " not found"));
    }
    
    // TODO: you can use CartItemDto as method argument instead of cardId, incomingCartItem
    @Transactional
    @Override
    public CartItem addCartItem(Long cartId, CartItem incomingItem) {
        // Load cart and aggregate product
        Cart cart = findCartById(cartId);
        Product product = incomingItem.getProduct();
        
        // Normalize inputs (defensive coding)
        int requestedQty = Math.max(1, incomingItem.getQuantity());
        int availableQty = Math.max(0, product.getAvailableQuantity());
        
        if (availableQty == 0) throw new OutOfStockException(product.getId());
        
        // Find existing line in the cart for this product (or create new)
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, product.getId())
                                          .orElseGet(() -> {
                                              CartItem newItem = new CartItem();
                                              newItem.setProduct(product);
                                              cart.addCartItemAndLink(newItem);
                                              return newItem;
                                              // quantity and totalCartItemPrice set below
                                          });
        
        // Calculate new quantity and throw if available stock exceeded
        int alreadyInCartQty = Optional.ofNullable(item.getQuantity()).orElse(0);
        int newQty = alreadyInCartQty + requestedQty;
        if (newQty > availableQty)
            throw new InsufficientStockException(product.getId(), availableQty, requestedQty, alreadyInCartQty);
        
        // Update quantity
        item.setQuantity(newQty);
        
        // Update items price
        BigDecimal itemsPrice = BigDecimal.valueOf(newQty).multiply(product.getPrice());
        item.setTotalPrice(itemsPrice);
        
        // Calculate price for requested items and Update totalCartPrice
        BigDecimal requestedItemsPrice = product.getPrice().multiply(BigDecimal.valueOf(requestedQty));
        cart.setTotalPrice(cart.getTotalPrice().add(requestedItemsPrice));
        return item;
    }
    
    @Transactional
    @Override
    public void removeCartItem(Long cartId, Long itemId) {
        CartItem item = cartItemRepository.findByCartIdAndId(cartId, itemId)
                                          .orElseThrow(() -> new EntityNotFoundException(
                                              "CartItem " + itemId + " not found in cart " + cartId));
        BigDecimal itemsPrice = item.getTotalPrice();
        
        Cart cart = item.getCart();
        cart.setTotalPrice(cart.getTotalPrice().subtract(itemsPrice).max(BigDecimal.ZERO));
        cart.removeCartItemAndUnlink(item);
    }
    
    @Override
    public CartItem incrementCartItemQty(Long cartId, Long itemId) {
        return null;
    }
    
    @Override
    public CartItem decrementCartItemQty(Long cartId, Long itemId) {
        return null;
    }
    
    @Override
    public void removeAllCartItems(Long cartId) {
    
    }
    
    @Override
    public BigDecimal getCartTotalPrice(Long cartId) {
        return null;
    }
}

