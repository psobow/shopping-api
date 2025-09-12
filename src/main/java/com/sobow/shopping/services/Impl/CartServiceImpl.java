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
    public Cart findCartById(Long id) {
        return cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart with " + id + " not found"));
    }
    
    // TODO: you can use CartItemDto as method argument instead of cardId, incomingCartItem
    @Transactional
    @Override
    public CartItem addCartItem(Long cartId, CartItem incomingCartItem) {
        // Load cart and aggregate product
        Cart cart = findCartById(cartId);
        Product product = incomingCartItem.getProduct();
        
        // Normalize inputs (defensive coding)
        int requestedQty = Math.max(1, incomingCartItem.getQuantity());
        int availableQty = Math.max(0, product.getAvailableQuantity());
        
        if (availableQty == 0) throw new OutOfStockException(product.getId());
        
        // Find existing line in the cart for this product (or create new)
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, product.getId())
                                              .orElseGet(() -> {
                                                  CartItem newCartItem = new CartItem();
                                                  newCartItem.setProduct(product);
                                                  cart.addCartItemAndLink(newCartItem);
                                                  return newCartItem;
                                                  // quantity and totalCartItemPrice set below
                                              });
        
        // Calculate new quantity and throw if available stock exceeded
        int alreadyInCartQty = Optional.ofNullable(cartItem.getQuantity()).orElse(0);
        int newQty = alreadyInCartQty + requestedQty;
        if (newQty > availableQty)
            throw new InsufficientStockException(product.getId(), availableQty, requestedQty, alreadyInCartQty);
        
        // Set new quantity
        cartItem.setQuantity(newQty);
        
        // Calculate and set totalCartItemPrice
        BigDecimal totalCartItemPrice = BigDecimal.valueOf(newQty).multiply(product.getPrice());
        cartItem.setTotalCartItemPrice(totalCartItemPrice);
        
        // Calculate and set totalCartPrice
        BigDecimal totalCartPrice = cart.getTotalCartPrice().add(totalCartItemPrice);
        cart.setTotalCartPrice(totalCartPrice);
        return cartItem;
    }
    
    @Transactional
    @Override
    public CartItem removeCartItem(Long cartId, CartItem cartItem) {
        return null;
    }
    
    @Override
    public CartItem incrementCartItemQty(Long cartId, CartItem cartItem) {
        return null;
    }
    
    @Override
    public CartItem decrementCartItemQty(Long cartId, CartItem cartItem) {
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

