package com.sobow.shopping.domain.user.requests;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmailRequest(
    @NotBlank @Size(min = 6, max = 100) String oldPassword,
    @NotBlank @Email String newEmail
) {
    
    public UpdateEmailRequest {
        newEmail = normalizeEmail(newEmail);
    }
}
