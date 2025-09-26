package com.sobow.shopping.mappers.user.responses.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.mappers.user.responses.UserAuthorityResponseMapper;
import com.sobow.shopping.mappers.user.responses.UserProfileResponseMapper;
import com.sobow.shopping.mappers.user.responses.UserResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserResponseMapperImpl implements UserResponseMapper {
    
    private final UserProfileResponseMapper userProfileResponseMapper;
    private final UserAuthorityResponseMapper userAuthorityResponseMapper;
    
    @Override
    public UserResponse mapToDto(User user) {
        return UserResponse.builder()
                           .email(user.getEmail())
                           .userProfile(userProfileResponseMapper.mapToDto(user.getProfile()))
                           .authorities(user.getAuthorities().stream().map(userAuthorityResponseMapper::mapToDto).toList())
                           .build();
    }
}
