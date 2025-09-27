package com.sobow.shopping.controllers.user.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;

public record UserAuthorityResponse(
    String value
) {
    
    @JsonCreator(mode = Mode.DELEGATING)
    public UserAuthorityResponse(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String json() {
        return value;
    }
}
