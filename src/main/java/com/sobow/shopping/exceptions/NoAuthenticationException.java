package com.sobow.shopping.exceptions;

public class NoAuthenticationException extends RuntimeException {
    
    public NoAuthenticationException() {
        super("Authentication required.");
    }
    
    public NoAuthenticationException(String message) {
        super(message);
    }
}