package com.sobow.shopping.exceptions;

import java.math.BigDecimal;

public class InvalidPriceException extends RuntimeException {
    
    private final BigDecimal price;
    
    public InvalidPriceException(BigDecimal price) {
        super("Price must be greater than 0. Provided: " + price);
        this.price = price;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
}