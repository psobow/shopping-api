package com.sobow.shopping.exceptions;

public class OverDecrementException extends RuntimeException {
    
    private final Long productId;
    private final int newQty;
    
    public OverDecrementException(Long productId, int newQty) {
        super("Cannot set negative available quantity: " + newQty + " of product " + productId);
        this.productId = productId;
        this.newQty = newQty;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public int getNewQty() {
        return newQty;
    }
}