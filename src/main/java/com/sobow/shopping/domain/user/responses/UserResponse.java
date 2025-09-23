package com.sobow.shopping.domain.user.responses;

import java.util.List;
import lombok.Builder;

@Builder
public record UserResponse(
    String email,
    UserProfileResponse userProfile,
    List<UserAuthorityResponse> authorities
) {

}
