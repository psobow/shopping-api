package com.sobow.shopping.controllers.user.requests.admin;

import static com.sobow.shopping.validation.ValidationUtils.normalizeAuthority;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserAuthorityRequest(
    @NotBlank @Size(max = 50) String role
) {
    
    @JsonCreator(mode = Mode.DELEGATING)
    public UserAuthorityRequest(String role) {
        this.role = normalizeAuthority(role);
    }
    
    @JsonValue
    public String json() {
        return role;
    }
}
