package com.sobow.shopping.domain.user.requests.admin;

import static com.sobow.shopping.validation.ValidationUtils.normalizeAuthority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorityDto(
    @NotBlank @Size(max = 50) String value
) {
    
    public AuthorityDto {
        value = normalizeAuthority(value);
    }
}
