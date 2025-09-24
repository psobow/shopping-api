package com.sobow.shopping.domain.user.requests.self;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.domain.user.requests.dto.PasswordDto;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SelfCreateUserRequest(
    @NotBlank @Email String email,
    @NotBlank @Valid PasswordDto password,
    @NotNull @Valid CreateUserProfileRequest userProfile
) {
    
    public SelfCreateUserRequest {
        email = normalizeEmail(email);
    }
}


