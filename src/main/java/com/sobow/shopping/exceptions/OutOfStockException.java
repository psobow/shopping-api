package com.sobow.shopping.exceptions;

public class OutOfStockException extends RuntimeException {
    
    private final Long productId;
    
    public OutOfStockException(Long productId) {
        super("Product " + productId + " is out of stock");
        this.productId = productId;
    }
    
    public Long getProductId() {
        return productId;
    }
}
