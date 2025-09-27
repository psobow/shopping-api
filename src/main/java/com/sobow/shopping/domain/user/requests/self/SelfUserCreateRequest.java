package com.sobow.shopping.domain.user.requests.self;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.domain.user.requests.PasswordRequest;
import com.sobow.shopping.domain.user.requests.UserProfileCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SelfUserCreateRequest(
    @NotBlank @Email String email,
    @NotNull @Valid PasswordRequest password,
    @NotNull @Valid UserProfileCreateRequest userProfile
) {
    
    public SelfUserCreateRequest {
        email = normalizeEmail(email);
    }
}


