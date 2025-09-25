package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.requests.shared.CreateUserAddressDto;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component("createUserAddressDtoMapper")
public class CreateUserAddressDtoMapper implements Mapper<UserAddress, CreateUserAddressDto> {
    
    @Override
    public UserAddress mapToEntity(CreateUserAddressDto addressRequest) {
        return UserAddress.builder()
                          .cityName(addressRequest.cityName())
                          .streetName(addressRequest.streetName())
                          .streetNumber(addressRequest.streetNumber())
                          .postCode(addressRequest.postCode())
                          .build();
    }
    
    @Override
    public CreateUserAddressDto mapToDto(UserAddress userAddress) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
