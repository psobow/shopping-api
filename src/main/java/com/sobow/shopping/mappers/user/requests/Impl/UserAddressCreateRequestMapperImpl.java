package com.sobow.shopping.mappers.user.requests.Impl;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.requests.UserAddressCreateRequest;
import com.sobow.shopping.mappers.user.requests.UserAddressCreateRequestMapper;
import org.springframework.stereotype.Component;

@Component
public class UserAddressCreateRequestMapperImpl implements UserAddressCreateRequestMapper {
    
    @Override
    public UserAddress mapToEntity(UserAddressCreateRequest addressRequest) {
        return UserAddress.builder()
                          .cityName(addressRequest.cityName())
                          .streetName(addressRequest.streetName())
                          .streetNumber(addressRequest.streetNumber())
                          .postCode(addressRequest.postCode())
                          .build();
    }
    
}
