package com.sobow.shopping.controllers.security.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtAuthRequest(
    @NotBlank String email,
    @NotBlank String password
) {

}
