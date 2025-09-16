package com.sobow.shopping.services;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;

public interface CartService {
    
    Cart createOrGetCartForUser(long userId);
    
    void removeCartForUser(long userId);
    
    Cart findById(long id);
    
    CartItem createCartItem(long cartId, CartItemCreateRequest dto);
    
    CartItem updateCartItemQty(long cartId, long itemId, CartItemUpdateRequest dto);
    
    void removeCartItem(long cartId, long itemId);
    
    void removeAllCartItems(long cartId);
    
}
