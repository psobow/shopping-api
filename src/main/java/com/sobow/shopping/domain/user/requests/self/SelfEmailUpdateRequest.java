package com.sobow.shopping.domain.user.requests.self;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.domain.user.requests.PasswordRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SelfEmailUpdateRequest(
    @NotBlank @Valid PasswordRequest oldPassword,
    @NotBlank @Email String newEmail
) {
    
    public SelfEmailUpdateRequest {
        newEmail = normalizeEmail(newEmail);
    }
}
