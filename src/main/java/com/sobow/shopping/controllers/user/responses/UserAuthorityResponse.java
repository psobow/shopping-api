package com.sobow.shopping.controllers.user.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;

public record UserAuthorityResponse(
    String role
) {
    
    @JsonCreator(mode = Mode.DELEGATING)
    public UserAuthorityResponse(String role) {
        this.role = role;
    }
    
    @JsonValue
    public String json() {
        return role;
    }
}
