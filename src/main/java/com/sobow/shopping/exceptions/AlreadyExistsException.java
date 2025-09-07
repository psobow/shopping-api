package com.sobow.shopping.exceptions;

public class AlreadyExistsException extends RuntimeException {
    
    public AlreadyExistsException(String resource, String field, String value) {
        super("%s with %s '%s' already exists".formatted(resource, field, value));
    }
}
