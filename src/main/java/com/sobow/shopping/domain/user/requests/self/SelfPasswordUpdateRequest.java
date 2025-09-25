package com.sobow.shopping.domain.user.requests.self;

import com.sobow.shopping.domain.user.requests.PasswordRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record SelfPasswordUpdateRequest(
    @NotBlank @Valid PasswordRequest oldPassword,
    @NotBlank @Valid PasswordRequest newPassword
) {
    
}
