package com.sobow.shopping.controllers.user.responses;

import java.util.List;
import lombok.Builder;

@Builder
public record UserResponse(
    String email,
    UserProfileResponse userProfile,
    List<UserAuthorityResponse> authorities
) {

}
