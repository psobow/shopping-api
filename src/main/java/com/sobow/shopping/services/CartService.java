package com.sobow.shopping.services;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;
import java.math.BigDecimal;

public interface CartService {
    
    Cart createCartForUser(long userId);
    
    void removeCartForUser(long userId);
    
    Cart findCartById(long id);
    
    CartItem createCartItem(long cartId, CartItemCreateRequest dto);
    
    void updateCartItemQty(long cartId, CartItemUpdateRequest dto);
    
    CartItem findCartItemByCartIdAndId(long cartId, long itemId);
    
    void removeCartItem(long cartId, long itemId);
    
    void removeAllCartItems(long cartId);
    
    BigDecimal getCartTotalPrice(long cartId);
    
    BigDecimal getCartItemTotalPrice(long cartId, long itemId);
}
