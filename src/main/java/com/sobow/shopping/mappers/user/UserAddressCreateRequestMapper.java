package com.sobow.shopping.mappers.user;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.requests.UserAddressCreateRequest;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserAddressCreateRequestMapper implements Mapper<UserAddress, UserAddressCreateRequest> {
    
    @Override
    public UserAddress mapToEntity(UserAddressCreateRequest addressRequest) {
        return UserAddress.builder()
                          .cityName(addressRequest.cityName())
                          .streetName(addressRequest.streetName())
                          .streetNumber(addressRequest.streetNumber())
                          .postCode(addressRequest.postCode())
                          .build();
    }
    
    @Override
    public UserAddressCreateRequest mapToDto(UserAddress userAddress) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
