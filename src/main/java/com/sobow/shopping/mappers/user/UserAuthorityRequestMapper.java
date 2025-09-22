package com.sobow.shopping.mappers.user;

import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.dto.UserAuthorityRequest;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserAuthorityRequestMapper implements Mapper<UserAuthority, UserAuthorityRequest> {
    
    @Override
    public UserAuthority mapToEntity(UserAuthorityRequest userAuthorityRequest) {
        return new UserAuthority(userAuthorityRequest.authority());
    }
    
    @Override
    public UserAuthorityRequest mapToDto(UserAuthority userAuthority) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
