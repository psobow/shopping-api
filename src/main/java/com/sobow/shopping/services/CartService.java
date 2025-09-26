package com.sobow.shopping.services;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.dto.CartItemUpdateRequest;

public interface CartService {
    
    Cart findByUserIdWithItems(long id);
    
    Cart selfCreateOrGetCart();
    
    void selfRemoveCart();
    
    CartItem selfCreateCartItem(CartItemCreateRequest createRequest);
    
    CartItem selfUpdateCartItemQty(long itemId, CartItemUpdateRequest updateRequest);
    
    void selfRemoveCartItem(long itemId);
    
    void selfRemoveAllCartItems();
    
    boolean exists();
}
