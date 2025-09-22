package com.sobow.shopping.domain.user.dto;

import static com.sobow.shopping.validation.ValidationUtils.POLAND_POST_CODE_REGEX;
import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserAddressCreateRequest(
    @NotBlank @Size(max = 80) String cityName,
    @NotBlank @Size(max = 80) String streetName,
    @NotBlank @Size(max = 20) String streetNumber,
    @NotBlank @Pattern(regexp = POLAND_POST_CODE_REGEX) String postCode
) {
    
    public UserAddressCreateRequest {
        cityName = normalizeSingleLine(cityName);
        streetName = normalizeSingleLine(streetName);
        streetNumber = normalizeSingleLine(streetNumber);
        postCode = normalizeSingleLine(postCode);
    }
}
