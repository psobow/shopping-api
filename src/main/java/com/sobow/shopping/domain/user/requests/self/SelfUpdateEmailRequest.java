package com.sobow.shopping.domain.user.requests.self;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.domain.user.requests.shared.PasswordDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SelfUpdateEmailRequest(
    @NotBlank @Valid PasswordDto oldPassword,
    @NotBlank @Email String newEmail
) {
    
    public SelfUpdateEmailRequest {
        newEmail = normalizeEmail(newEmail);
    }
}
