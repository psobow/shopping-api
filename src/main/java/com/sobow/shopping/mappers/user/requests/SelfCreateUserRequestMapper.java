package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.self.SelfCreateUserRequest;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileDto;
import com.sobow.shopping.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("selfCreateUserRequestMapper")
public class SelfCreateUserRequestMapper implements Mapper<User, SelfCreateUserRequest> {
    
    @Qualifier("createUserProfileDtoMapper")
    private final Mapper<UserProfile, CreateUserProfileDto> createUserProfileDtoMapper;
    
    @Override
    public User mapToEntity(SelfCreateUserRequest userCreateRequest) {
        User user = User.builder()
                        .email(userCreateRequest.email())
                        .password(userCreateRequest.password().value())
                        .build();
        
        UserProfile userProfile = createUserProfileDtoMapper.mapToEntity(userCreateRequest.userProfile());
        user.setProfileAndLink(userProfile);
        
        return user;
    }
    
    @Override
    public SelfCreateUserRequest mapToDto(User user) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
