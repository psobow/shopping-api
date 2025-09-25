package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;
import com.sobow.shopping.domain.user.requests.admin.AuthorityDto;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileDto;
import com.sobow.shopping.mappers.Mapper;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("adminCreateUserRequestMapper")
public class AdminCreateUserRequestMapper implements Mapper<User, AdminCreateUserRequest> {
    
    @Qualifier("userAuthorityDtoMapper")
    private final Mapper<UserAuthority, AuthorityDto> userAuthorityDtoMapper;
    
    @Qualifier("createUserProfileDtoMapper")
    private final Mapper<UserProfile, CreateUserProfileDto> createUserProfileDtoMapper;
    
    @Override
    public User mapToEntity(AdminCreateUserRequest userCreateRequest) {
        
        Set<UserAuthority> authorities = userCreateRequest.authorities()
                                                          .value()
                                                          .stream()
                                                          .distinct()
                                                          .map(userAuthorityDtoMapper::mapToEntity)
                                                          .collect(Collectors.toSet());
        User user = User.builder()
                        .email(userCreateRequest.email())
                        .password(userCreateRequest.password().value())
                        .build()
                        .withAuthorities(authorities);
        
        UserProfile userProfile = createUserProfileDtoMapper.mapToEntity(userCreateRequest.userProfile());
        user.setProfileAndLink(userProfile);
        
        return user;
    }
    
    @Override
    public AdminCreateUserRequest mapToDto(User user) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
