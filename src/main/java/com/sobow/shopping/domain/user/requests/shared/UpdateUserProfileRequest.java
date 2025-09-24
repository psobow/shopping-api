package com.sobow.shopping.domain.user.requests.shared;

import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
    @Size(min = 1, max = 60) String firstName,
    @Size(min = 1, max = 60) String lastName,
    @Valid UpdateUserAddressRequest userAddress
) {
    
    public UpdateUserProfileRequest {
        firstName = normalizeSingleLine(firstName);
        lastName = normalizeSingleLine(lastName);
    }
}
