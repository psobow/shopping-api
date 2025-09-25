package com.sobow.shopping.domain.user.requests.self;

import com.sobow.shopping.domain.user.requests.UserProfileUpdateRequest;
import jakarta.validation.Valid;

public record SelfUserPartialUpdateRequest(
    @Valid UserProfileUpdateRequest userProfile
) {
    
}
