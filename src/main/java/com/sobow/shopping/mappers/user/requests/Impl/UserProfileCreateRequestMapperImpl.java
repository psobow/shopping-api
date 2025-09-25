package com.sobow.shopping.mappers.user.requests.Impl;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.UserProfileCreateRequest;
import com.sobow.shopping.mappers.user.requests.UserAddressCreateRequestMapper;
import com.sobow.shopping.mappers.user.requests.UserProfileCreateRequestMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class UserProfileCreateRequestMapperImpl implements UserProfileCreateRequestMapper {
    
    private final UserAddressCreateRequestMapper userAddressCreateRequestMapper;
    
    @Override
    public UserProfile mapToEntity(UserProfileCreateRequest userProfileCreateRequest) {
        UserAddress address = userAddressCreateRequestMapper.mapToEntity(userProfileCreateRequest.userAddress());
        UserProfile userProfile = UserProfile.builder()
                                             .firstName(userProfileCreateRequest.firstName())
                                             .lastName(userProfileCreateRequest.lastName())
                                             .build();
        userProfile.setAddressAndLink(address);
        return userProfile;
    }
    
    @Override
    public UserProfileCreateRequest mapToDto(UserProfile userProfile) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
