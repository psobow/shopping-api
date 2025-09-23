package com.sobow.shopping.domain.user.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeleteUserRequest(
    @NotBlank @Size(min = 6, max = 100) String oldPassword
) {
    
}
