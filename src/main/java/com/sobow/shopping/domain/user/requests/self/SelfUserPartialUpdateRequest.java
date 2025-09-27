package com.sobow.shopping.domain.user.requests.self;

import com.sobow.shopping.domain.user.requests.UserProfileUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SelfUserPartialUpdateRequest(
    @NotNull @Valid UserProfileUpdateRequest userProfile
) {
    
}
