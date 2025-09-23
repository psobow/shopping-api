package com.sobow.shopping.domain.user.requests;

import com.sobow.shopping.validation.annotations.Distinct;
import com.sobow.shopping.validation.annotations.ValidRoles;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UserUpdateRequest(
    @Valid UserProfileUpdateRequest userProfile,
    @Distinct @ValidRoles @Size(max = 10) List<UserAuthorityRequest> authorities
) {
    
}
