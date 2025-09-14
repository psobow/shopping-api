package com.sobow.shopping.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {
    
    public ProductAlreadyExistsException(String name, String brandName) {
        super("Product with name '%s' and brand name '%s' already exists".formatted(name, brandName));
    }
}
