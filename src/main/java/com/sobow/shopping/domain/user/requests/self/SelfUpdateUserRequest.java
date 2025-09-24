package com.sobow.shopping.domain.user.requests.self;

import com.sobow.shopping.domain.user.requests.shared.UpdateUserProfileRequest;
import jakarta.validation.Valid;

public record SelfUpdateUserRequest(
    @Valid UpdateUserProfileRequest userProfile
) {
    
}
