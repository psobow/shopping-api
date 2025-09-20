package com.sobow.shopping.exceptions;

public class CartEmptyException extends RuntimeException {
    
    public CartEmptyException(Long cartId) {
        super("Cannot create order: cart " + cartId + " is empty.");
    }
}