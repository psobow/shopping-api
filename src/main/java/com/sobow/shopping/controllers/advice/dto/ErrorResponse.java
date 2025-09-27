package com.sobow.shopping.controllers.advice.dto;

import java.util.List;

public record ErrorResponse(
    int status,
    String error,
    String message,
    List<FieldViolation> violations
) {
    
    public record FieldViolation(String field, String message) {
    
    }
}
