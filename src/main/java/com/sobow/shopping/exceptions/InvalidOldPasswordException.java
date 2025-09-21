package com.sobow.shopping.exceptions;

public class InvalidOldPasswordException extends RuntimeException {
    
    public InvalidOldPasswordException() {
        super("Invalid old password.");
    }
    
    public InvalidOldPasswordException(String message) {
        super(message);
    }
}