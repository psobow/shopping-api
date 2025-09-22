package com.sobow.shopping.services;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.dto.CartItemUpdateRequest;

public interface CartService {
    
    Cart createOrGetCart(long userId);
    
    void removeCart(long userId);
    
    Cart findById(long id);
    
    Cart findByUserIdWithItems(long id);
    
    CartItem createCartItem(long cartId, CartItemCreateRequest createRequest);
    
    CartItem updateCartItemQty(long cartId, long itemId, CartItemUpdateRequest updateRequest);
    
    void removeCartItem(long cartId, long itemId);
    
    void removeAllCartItems(long cartId);
    
    boolean existsByUserProfile_UserId(long userId);
    
}
