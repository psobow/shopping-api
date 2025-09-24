package com.sobow.shopping.domain.user.requests.admin;

import static com.sobow.shopping.validation.ValidationUtils.normalizeAuthority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserAuthorityRequest(
    @NotBlank @Size(max = 50) String authority
) {
    
    public UserAuthorityRequest {
        authority = normalizeAuthority(authority);
    }
}
