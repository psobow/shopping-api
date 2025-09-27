package com.sobow.shopping.controllers.user.requests.self;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.controllers.user.requests.PasswordRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SelfEmailUpdateRequest(
    @NotNull @Valid PasswordRequest oldPassword,
    @NotBlank @Email String newEmail
) {
    
    public SelfEmailUpdateRequest {
        newEmail = normalizeEmail(newEmail);
    }
}
