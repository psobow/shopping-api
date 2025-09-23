package com.sobow.shopping.mappers.user;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.UserAuthorityRequest;
import com.sobow.shopping.domain.user.requests.UserCreateRequest;
import com.sobow.shopping.domain.user.requests.UserProfileCreateRequest;
import com.sobow.shopping.mappers.Mapper;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserCreateRequestMapper implements Mapper<User, UserCreateRequest> {
    
    private final Mapper<UserAuthority, UserAuthorityRequest> userAuthorityRequestMapper;
    private final Mapper<UserProfile, UserProfileCreateRequest> userProfileRequestMapper;
    
    @Override
    public User mapToEntity(UserCreateRequest userCreateRequest) {
        
        Set<UserAuthority> authorities = userCreateRequest.authorities()
                                                          .stream()
                                                          .map(userAuthorityRequestMapper::mapToEntity)
                                                          .collect(Collectors.toSet());
        User user = User.builder()
                        .email(userCreateRequest.email())
                        .password(userCreateRequest.password())
                        .build()
                        .withAuthorities(authorities);
        
        UserProfile userProfile = userProfileRequestMapper.mapToEntity(userCreateRequest.userProfile());
        user.setProfileAndLink(userProfile);
        
        return user;
    }
    
    @Override
    public UserCreateRequest mapToDto(User user) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
