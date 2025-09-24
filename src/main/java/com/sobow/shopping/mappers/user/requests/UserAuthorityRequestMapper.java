package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.requests.admin.AuthorityDto;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserAuthorityRequestMapper implements Mapper<UserAuthority, AuthorityDto> {
    
    @Override
    public UserAuthority mapToEntity(AuthorityDto authorityDto) {
        return new UserAuthority(authorityDto.authority());
    }
    
    @Override
    public AuthorityDto mapToDto(UserAuthority userAuthority) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
