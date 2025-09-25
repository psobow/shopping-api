package com.sobow.shopping.services;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.dto.CartItemUpdateRequest;

public interface CartService {
    
    Cart selfCreateOrGetCart();
    
    void selfRemoveCart();
    
    Cart findByIdWithItems(long id);
    
    Cart findByUserIdWithItems(long id);
    
    CartItem selfCreateCartItem(CartItemCreateRequest createRequest);
    
    CartItem selfUpdateCartItemQty(long itemId, CartItemUpdateRequest updateRequest);
    
    void selfRemoveCartItem(long itemId);
    
    void selfRemoveAllCartItems();
    
    boolean exists();
    
}
