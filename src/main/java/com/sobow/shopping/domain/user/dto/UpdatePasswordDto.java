package com.sobow.shopping.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDto(
    @NotBlank @Size(min = 6, max = 100) String oldPassword,
    @NotBlank @Size(min = 6, max = 100) String newPassword
) {
    
}
