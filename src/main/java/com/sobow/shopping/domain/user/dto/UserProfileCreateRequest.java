package com.sobow.shopping.domain.user.dto;

import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserProfileCreateRequest(
    @NotBlank @Size(max = 60) String firstName,
    @NotBlank @Size(max = 60) String lastName,
    @NotNull @Valid UserAddressCreateRequest userAddress
) {
    
    public UserProfileCreateRequest {
        firstName = normalizeSingleLine(firstName);
        lastName = normalizeSingleLine(lastName);
    }
}
