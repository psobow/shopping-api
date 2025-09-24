package com.sobow.shopping.domain.user.requests.shared;

import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserProfileDto(
    @NotBlank @Size(max = 60) String firstName,
    @NotBlank @Size(max = 60) String lastName,
    @NotNull @Valid CreateUserAddressDto userAddress
) {
    
    public CreateUserProfileDto {
        firstName = normalizeSingleLine(firstName);
        lastName = normalizeSingleLine(lastName);
    }
}
