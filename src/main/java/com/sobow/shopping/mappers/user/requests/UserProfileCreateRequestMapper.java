package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.UserAddressCreateRequest;
import com.sobow.shopping.domain.user.requests.UserProfileCreateRequest;
import com.sobow.shopping.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class UserProfileCreateRequestMapper implements Mapper<UserProfile, UserProfileCreateRequest> {
    
    private final Mapper<UserAddress, UserAddressCreateRequest> userAddressRequestMapper;
    
    @Override
    public UserProfile mapToEntity(UserProfileCreateRequest userProfileCreateRequest) {
        UserAddress address = userAddressRequestMapper.mapToEntity(userProfileCreateRequest.userAddress());
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
