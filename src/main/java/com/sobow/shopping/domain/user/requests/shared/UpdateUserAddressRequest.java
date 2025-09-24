package com.sobow.shopping.domain.user.requests.shared;

import static com.sobow.shopping.validation.ValidationUtils.POLAND_POST_CODE_REGEX;
import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserAddressRequest(
    @Size(min = 1, max = 80) String cityName,
    @Size(min = 1, max = 80) String streetName,
    @Size(min = 1, max = 20) String streetNumber,
    @Pattern(regexp = POLAND_POST_CODE_REGEX) String postCode
) {
    
    public UpdateUserAddressRequest {
        cityName = normalizeSingleLine(cityName);
        streetName = normalizeSingleLine(streetName);
        streetNumber = normalizeSingleLine(streetNumber);
        postCode = normalizeSingleLine(postCode);
    }
}
