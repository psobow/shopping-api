package com.sobow.shopping.controllers.user.requests.self;

import com.sobow.shopping.controllers.user.requests.PasswordRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SelfPasswordUpdateRequest(
    @NotNull @Valid PasswordRequest password,
    @NotNull @Valid PasswordRequest newPassword
) {
    
}
