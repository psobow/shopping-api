package com.sobow.shopping.exceptions;

public class InsufficientStockException extends RuntimeException {
    
    private final Long productId;
    private final int available;
    private final int requested;
    private final int alreadyInCart;
    
    public InsufficientStockException(Long productId, int available, int requested, int alreadyInCart) {
        super("Requested quantity exceeds stock for product " + productId +
                  " (available=" + available + ", requested=" + requested +
                  ", alreadyInCart=" + alreadyInCart + ")");
        this.productId = productId;
        this.available = available;
        this.requested = requested;
        this.alreadyInCart = alreadyInCart;
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
    
    public int getAlreadyInCart() {
        return alreadyInCart;
    }
}
