package com.sobow.shopping.domain.user.requests;

import jakarta.validation.Valid;

public record SelfUserUpdateRequest(
    @Valid SelfUserProfileUpdateRequest userProfile
) {
    
}
