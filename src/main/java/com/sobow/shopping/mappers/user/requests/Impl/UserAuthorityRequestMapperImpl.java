package com.sobow.shopping.mappers.user.requests.Impl;

import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.requests.admin.UserAuthorityRequest;
import com.sobow.shopping.mappers.user.requests.UserAuthorityRequestMapper;
import org.springframework.stereotype.Component;

@Component
public class UserAuthorityRequestMapperImpl implements UserAuthorityRequestMapper {
    
    @Override
    public UserAuthority mapToEntity(UserAuthorityRequest userAuthorityRequest) {
        return new UserAuthority(userAuthorityRequest.role());
    }
    
}
