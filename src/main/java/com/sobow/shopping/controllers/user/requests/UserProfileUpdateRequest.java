package com.sobow.shopping.controllers.user.requests;

import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
    @Size(min = 1, max = 60) String firstName,
    @Size(min = 1, max = 60) String lastName,
    @Valid UserAddressUpdateRequest userAddress
) {
    
    public UserProfileUpdateRequest {
        firstName = normalizeSingleLine(firstName);
        lastName = normalizeSingleLine(lastName);
    }
}
