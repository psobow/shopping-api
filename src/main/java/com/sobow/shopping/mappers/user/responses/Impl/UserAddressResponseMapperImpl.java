package com.sobow.shopping.mappers.user.responses.Impl;

import com.sobow.shopping.controllers.user.responses.UserAddressResponse;
import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.mappers.user.responses.UserAddressResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class UserAddressResponseMapperImpl implements UserAddressResponseMapper {
    
    @Override
    public UserAddressResponse mapToDto(UserAddress userAddress) {
        return UserAddressResponse.builder()
                                  .cityName(userAddress.getCityName())
                                  .streetName(userAddress.getStreetName())
                                  .streetNumber(userAddress.getStreetNumber())
                                  .postCode(userAddress.getPostCode())
                                  .build();
    }
}
