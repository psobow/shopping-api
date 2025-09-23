package com.sobow.shopping.mappers.user.responses;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.responses.UserAddressResponse;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserAddressResponseMapper implements Mapper<UserAddress, UserAddressResponse> {
    
    @Override
    public UserAddress mapToEntity(UserAddressResponse addressResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
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
