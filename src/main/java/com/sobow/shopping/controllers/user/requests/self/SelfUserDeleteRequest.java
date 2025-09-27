package com.sobow.shopping.controllers.user.requests.self;

import com.sobow.shopping.controllers.user.requests.PasswordRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SelfUserDeleteRequest(
    @NotNull @Valid PasswordRequest oldPassword
) {
    
}
