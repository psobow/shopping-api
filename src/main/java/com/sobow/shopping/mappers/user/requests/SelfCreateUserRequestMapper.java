package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.self.SelfCreateUserRequest;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileRequest;
import com.sobow.shopping.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SelfCreateUserRequestMapper implements Mapper<User, SelfCreateUserRequest> {
    
    private final Mapper<UserProfile, CreateUserProfileRequest> userProfileRequestMapper;
    
    @Override
    public User mapToEntity(SelfCreateUserRequest userCreateRequest) {
        User user = User.builder()
                        .email(userCreateRequest.email())
                        .password(userCreateRequest.password().value())
                        .build();
        
        UserProfile userProfile = userProfileRequestMapper.mapToEntity(userCreateRequest.userProfile());
        user.setProfileAndLink(userProfile);
        
        return user;
    }
    
    @Override
    public SelfCreateUserRequest mapToDto(User user) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
