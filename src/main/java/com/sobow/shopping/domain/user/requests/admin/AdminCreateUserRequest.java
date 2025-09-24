package com.sobow.shopping.domain.user.requests.admin;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.domain.user.requests.dto.AuthoritiesDto;
import com.sobow.shopping.domain.user.requests.dto.PasswordDto;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCreateUserRequest(
    @NotBlank @Email String email,
    @NotBlank @Valid PasswordDto password,
    @NotNull @Valid CreateUserProfileRequest userProfile,
    @NotNull @Valid AuthoritiesDto authorities
) {
    
    public AdminCreateUserRequest {
        email = normalizeEmail(email);
    }
}
