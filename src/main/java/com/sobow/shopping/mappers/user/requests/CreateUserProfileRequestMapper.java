package com.sobow.shopping.mappers.user.requests;

import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.shared.CreateUserAddressRequest;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileRequest;
import com.sobow.shopping.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CreateUserProfileRequestMapper implements Mapper<UserProfile, CreateUserProfileRequest> {
    
    private final Mapper<UserAddress, CreateUserAddressRequest> userAddressRequestMapper;
    
    @Override
    public UserProfile mapToEntity(CreateUserProfileRequest createUserProfileRequest) {
        UserAddress address = userAddressRequestMapper.mapToEntity(createUserProfileRequest.userAddress());
        UserProfile userProfile = UserProfile.builder()
                                             .firstName(createUserProfileRequest.firstName())
                                             .lastName(createUserProfileRequest.lastName())
                                             .build();
        userProfile.setAddressAndLink(address);
        return userProfile;
    }
    
    @Override
    public CreateUserProfileRequest mapToDto(UserProfile userProfile) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
