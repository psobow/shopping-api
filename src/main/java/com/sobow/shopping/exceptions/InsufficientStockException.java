package com.sobow.shopping.exceptions;

public class InsufficientStockException extends RuntimeException {
    
    private final Long productId;
    private final int available;
    private final int requested;
    
    public InsufficientStockException(Long productId, int available, int requested) {
        super("Requested quantity exceeds stock for product id " + productId +
                  " available " + available + ", requested " + requested);
        this.productId = productId;
        this.available = available;
        this.requested = requested;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public int getAvailable() {
        return available;
    }
    
    public int getRequested() {
        return requested;
    }
}
