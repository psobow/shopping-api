package com.sobow.shopping.mappers.user.requests.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.mappers.user.requests.SelfUserCreateRequestMapper;
import com.sobow.shopping.mappers.user.requests.UserProfileCreateRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SelfUserCreateRequestMapperImpl implements SelfUserCreateRequestMapper {
    
    private final UserProfileCreateRequestMapper userProfileCreateRequestMapper;
    
    @Override
    public User mapToEntity(SelfUserCreateRequest userCreateRequest) {
        User user = User.builder()
                        .email(userCreateRequest.email())
                        .password(userCreateRequest.password().value())
                        .build();
        
        UserProfile userProfile = userProfileCreateRequestMapper.mapToEntity(userCreateRequest.userProfile());
        user.setProfileAndLink(userProfile);
        
        return user;
    }
    
}
