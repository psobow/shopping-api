package com.sobow.shopping.services;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;
import java.math.BigDecimal;

public interface CartService {
    
    Cart createCartForUser(Long userId);
    
    void removeCartForUser(Long userId);
    
    Cart findCartById(Long id);
    
    CartItem createCartItem(Long cartId, CartItemCreateRequest dto);
    
    void updateCartItemQty(Long cartId, CartItemUpdateRequest dto);
    
    void removeCartItem(Long cartId, Long itemId);
    
    void removeAllCartItems(Long cartId);
    
    BigDecimal getCartTotalPrice(Long cartId);
    
    BigDecimal getCartItemTotalPrice(Long cartId, Long itemId);
}
