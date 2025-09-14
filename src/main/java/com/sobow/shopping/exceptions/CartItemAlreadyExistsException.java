package com.sobow.shopping.exceptions;

public class CartItemAlreadyExistsException extends RuntimeException {
    
    private final Long cartId;
    private final Long productId;
    
    public CartItemAlreadyExistsException(Long cartId, Long productId) {
        super("Product " + productId + " is already in cart " + cartId);
        this.cartId = cartId;
        this.productId = productId;
    }
}
