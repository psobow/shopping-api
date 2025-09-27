package com.sobow.shopping.controllers.user.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordRequest(
    @NotBlank @Size(min = 6, max = 100) String value
) {
    
    @JsonCreator(mode = Mode.DELEGATING)
    public PasswordRequest(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String json() {
        return value;
    }
}
