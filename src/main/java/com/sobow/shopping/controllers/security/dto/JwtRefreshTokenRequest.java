package com.sobow.shopping.controllers.security.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtRefreshTokenRequest(@NotBlank String refresh) {

}
