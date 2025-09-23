package com.sobow.shopping.domain.user.requests;

import static com.sobow.shopping.validation.ValidationUtils.POLAND_POST_CODE_REGEX;
import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserAddressUpdateRequest(
    @Size(min = 1, max = 80) String cityName,
    @Size(min = 1, max = 80) String streetName,
    @Size(min = 1, max = 20) String streetNumber,
    @Pattern(regexp = POLAND_POST_CODE_REGEX) String postCode
) {
    
    public UserAddressUpdateRequest {
        cityName = normalizeSingleLine(cityName);
        streetName = normalizeSingleLine(streetName);
        streetNumber = normalizeSingleLine(streetNumber);
        postCode = normalizeSingleLine(postCode);
    }
}
