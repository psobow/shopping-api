package com.sobow.shopping.mappers.user.responses;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.responses.UserAuthorityResponse;
import com.sobow.shopping.domain.user.responses.UserProfileResponse;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("userResponseMapper")
public class UserResponseMapper implements Mapper<User, UserResponse> {
    
    @Qualifier("userProfileResponseMapper")
    private final Mapper<UserProfile, UserProfileResponse> userProfileResponseMapper;
    @Qualifier("userAuthorityResponseMapper")
    private final Mapper<UserAuthority, UserAuthorityResponse> userAuthorityResponseMapper;
    
    @Override
    public User mapToEntity(UserResponse userResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public UserResponse mapToDto(User user) {
        return UserResponse.builder()
                           .email(user.getEmail())
                           .userProfile(userProfileResponseMapper.mapToDto(user.getProfile()))
                           .authorities(user.getAuthorities().stream().map(userAuthorityResponseMapper::mapToDto).toList())
                           .build();
    }
}
