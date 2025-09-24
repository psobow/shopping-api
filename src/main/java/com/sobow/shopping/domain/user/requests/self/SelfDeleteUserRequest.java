package com.sobow.shopping.domain.user.requests.self;

import com.sobow.shopping.domain.user.requests.dto.PasswordDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record SelfDeleteUserRequest(
    @NotBlank @Valid PasswordDto oldPassword
    
) {
    
}
