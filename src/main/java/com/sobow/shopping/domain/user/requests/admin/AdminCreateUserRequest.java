package com.sobow.shopping.domain.user.requests.admin;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileDto;
import com.sobow.shopping.domain.user.requests.shared.PasswordDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCreateUserRequest(
    @NotBlank @Email String email,
    @NotBlank @Valid PasswordDto password,
    @NotNull @Valid CreateUserProfileDto userProfile,
    @NotNull @Valid ListAuthorityDto authorities
) {
    
    public AdminCreateUserRequest {
        email = normalizeEmail(email);
    }
}
