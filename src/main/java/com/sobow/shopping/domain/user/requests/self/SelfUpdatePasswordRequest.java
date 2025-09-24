package com.sobow.shopping.domain.user.requests.self;

import com.sobow.shopping.domain.user.requests.shared.PasswordDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record SelfUpdatePasswordRequest(
    @NotBlank @Valid PasswordDto oldPassword,
    @NotBlank @Valid PasswordDto newPassword
) {
    
}
