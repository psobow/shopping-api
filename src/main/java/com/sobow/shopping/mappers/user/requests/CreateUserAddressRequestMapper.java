package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.requests.shared.CreateUserAddressRequest;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CreateUserAddressRequestMapper implements Mapper<UserAddress, CreateUserAddressRequest> {
    
    @Override
    public UserAddress mapToEntity(CreateUserAddressRequest addressRequest) {
        return UserAddress.builder()
                          .cityName(addressRequest.cityName())
                          .streetName(addressRequest.streetName())
                          .streetNumber(addressRequest.streetNumber())
                          .postCode(addressRequest.postCode())
                          .build();
    }
    
    @Override
    public CreateUserAddressRequest mapToDto(UserAddress userAddress) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
