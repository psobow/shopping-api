package com.sobow.shopping.mappers.user.responses.Impl;

import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.responses.UserAuthorityResponse;
import com.sobow.shopping.mappers.user.responses.UserAuthorityResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class UserAuthorityResponseMapperImpl implements UserAuthorityResponseMapper {
    
    @Override
    public UserAuthority mapToEntity(UserAuthorityResponse userAuthorityResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public UserAuthorityResponse mapToDto(UserAuthority userAuthority) {
        return new UserAuthorityResponse(userAuthority.getValue());
    }
}
