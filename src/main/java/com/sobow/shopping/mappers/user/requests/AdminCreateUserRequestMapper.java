package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;
import com.sobow.shopping.domain.user.requests.admin.UserAuthorityRequest;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileRequest;
import com.sobow.shopping.mappers.Mapper;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminCreateUserRequestMapper implements Mapper<User, AdminCreateUserRequest> {
    
    private final Mapper<UserAuthority, UserAuthorityRequest> userAuthorityRequestMapper;
    private final Mapper<UserProfile, CreateUserProfileRequest> userProfileRequestMapper;
    
    @Override
    public User mapToEntity(AdminCreateUserRequest userCreateRequest) {
        
        Set<UserAuthority> authorities = userCreateRequest.authorities()
                                                          .value()
                                                          .stream()
                                                          .distinct()
                                                          .map(userAuthorityRequestMapper::mapToEntity)
                                                          .collect(Collectors.toSet());
        User user = User.builder()
                        .email(userCreateRequest.email())
                        .password(userCreateRequest.password().value())
                        .build()
                        .withAuthorities(authorities);
        
        UserProfile userProfile = userProfileRequestMapper.mapToEntity(userCreateRequest.userProfile());
        user.setProfileAndLink(userProfile);
        
        return user;
    }
    
    @Override
    public AdminCreateUserRequest mapToDto(User user) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
