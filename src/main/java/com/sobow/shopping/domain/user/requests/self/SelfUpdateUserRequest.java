package com.sobow.shopping.domain.user.requests.self;

import com.sobow.shopping.domain.user.requests.shared.UpdateUserProfileDto;
import jakarta.validation.Valid;

public record SelfUpdateUserRequest(
    @Valid UpdateUserProfileDto userProfile
) {
    
}
