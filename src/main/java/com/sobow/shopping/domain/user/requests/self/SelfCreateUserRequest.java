package com.sobow.shopping.domain.user.requests.self;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileDto;
import com.sobow.shopping.domain.user.requests.shared.PasswordDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SelfCreateUserRequest(
    @NotBlank @Email String email,
    @NotBlank @Valid PasswordDto password,
    @NotNull @Valid CreateUserProfileDto userProfile
) {
    
    public SelfCreateUserRequest {
        email = normalizeEmail(email);
    }
}


