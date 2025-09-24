package com.sobow.shopping.domain.user.requests;

import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record SelfUserProfileUpdateRequest(
    @Size(min = 1, max = 60) String firstName,
    @Size(min = 1, max = 60) String lastName,
    @Valid SelfUserAddressUpdateRequest userAddress
) {
    
    public SelfUserProfileUpdateRequest {
        firstName = normalizeSingleLine(firstName);
        lastName = normalizeSingleLine(lastName);
    }
}
