package com.sobow.shopping.mappers.user.requests.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.mappers.user.requests.AdminUserCreateRequestMapper;
import com.sobow.shopping.mappers.user.requests.UserAuthorityRequestMapper;
import com.sobow.shopping.mappers.user.requests.UserProfileCreateRequestMapper;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminUserCreateRequestMapperImpl implements AdminUserCreateRequestMapper {
    
    private final UserAuthorityRequestMapper userAuthorityRequestMapper;
    private final UserProfileCreateRequestMapper userProfileCreateRequestMapper;
    
    @Override
    public User mapToEntity(AdminUserCreateRequest userCreateRequest) {
        
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
        
        UserProfile userProfile = userProfileCreateRequestMapper.mapToEntity(userCreateRequest.userProfile());
        user.setProfileAndLink(userProfile);
        
        return user;
    }
    
}
