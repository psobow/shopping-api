package com.sobow.shopping.exceptions;

import com.sobow.shopping.domain.cart.Cart;

public class CartEmptyException extends RuntimeException {
    
    public CartEmptyException(Cart cart) {
        super("Cannot create order: cart " + cart.getId() + " is empty.");
    }
}