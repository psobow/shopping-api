package com.sobow.shopping.domain.user.requests.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Size;

public record PasswordDto(
    @Size(min = 6, max = 100) String value
) {
    
    @JsonCreator(mode = Mode.DELEGATING)
    public PasswordDto(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String json() {
        return value;
    }
}
