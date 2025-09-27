package com.sobow.shopping.mappers.user.responses.Impl;

import com.sobow.shopping.controllers.user.responses.UserAuthorityResponse;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.mappers.user.responses.UserAuthorityResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class UserAuthorityResponseMapperImpl implements UserAuthorityResponseMapper {
    
    @Override
    public UserAuthorityResponse mapToDto(UserAuthority userAuthority) {
        return new UserAuthorityResponse(userAuthority.getValue());
    }
}
