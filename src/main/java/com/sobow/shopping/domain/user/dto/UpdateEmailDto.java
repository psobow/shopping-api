package com.sobow.shopping.domain.user.dto;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmailDto(
    @NotBlank @Size(min = 6, max = 100) String oldPassword,
    @NotBlank @Email String newEmail
) {
    
    public UpdateEmailDto {
        newEmail = normalizeEmail(newEmail);
    }
}
