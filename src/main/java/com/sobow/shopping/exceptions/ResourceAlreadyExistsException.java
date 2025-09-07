package com.sobow.shopping.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String resource, String field, String value) {
        super("%s with %s '%s' already exists".formatted(resource, field, value));
    }
}
