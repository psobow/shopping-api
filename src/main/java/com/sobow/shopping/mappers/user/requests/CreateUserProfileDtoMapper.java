package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.shared.CreateUserAddressDto;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileDto;
import com.sobow.shopping.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("createUserProfileDtoMapper")
public class CreateUserProfileDtoMapper implements Mapper<UserProfile, CreateUserProfileDto> {
    
    @Qualifier("createUserAddressDtoMapper")
    private final Mapper<UserAddress, CreateUserAddressDto> createUserAddressDtoMapper;
    
    @Override
    public UserProfile mapToEntity(CreateUserProfileDto createUserProfileDto) {
        UserAddress address = createUserAddressDtoMapper.mapToEntity(createUserProfileDto.userAddress());
        UserProfile userProfile = UserProfile.builder()
                                             .firstName(createUserProfileDto.firstName())
                                             .lastName(createUserProfileDto.lastName())
                                             .build();
        userProfile.setAddressAndLink(address);
        return userProfile;
    }
    
    @Override
    public CreateUserProfileDto mapToDto(UserProfile userProfile) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
