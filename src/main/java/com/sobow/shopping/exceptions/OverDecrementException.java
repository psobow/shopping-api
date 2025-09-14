package com.sobow.shopping.exceptions;

public class OverDecrementException extends RuntimeException {
    
    private final Long productId;
    private final int currentQty;
    private final int requestedRemoval;
    
    public OverDecrementException(Long productId, int currentQty, int requestedRemoval) {
        super("Cannot remove " + requestedRemoval + " – only " + currentQty + " of product " + productId + " in stock");
        this.productId = productId;
        this.currentQty = currentQty;
        this.requestedRemoval = requestedRemoval;
    }
    
    public Long getProductId() {
        return productId;
    }
}