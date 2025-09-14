package com.sobow.shopping.services;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import java.math.BigDecimal;

public interface CartService {
    
    Cart createCartForUser(Long userId);
    
    void removeCartForUser(Long userId);
    
    Cart findCartById(Long id);
    
    CartItem addCartItem(Long cartId, CartItem item);
    
    void removeCartItem(Long cartId, Long itemId);
    
    void incrementCartItemQty(Long cartId, Long itemId, Integer deltaQty);
    
    void decrementCartItemQty(Long cartId, Long itemId, Integer deltaQty);
    
    void removeAllCartItems(Long cartId);
    
    BigDecimal getCartTotalPrice(Long cartId);
    
    BigDecimal getCartItemTotalPrice(Long cartId, Long itemId);
}
