package com.sobow.shopping.mappers.user.responses.Impl;

import com.sobow.shopping.domain.cart.dto.CartResponse;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.responses.UserProfileResponse;
import com.sobow.shopping.mappers.cart.CartResponseMapper;
import com.sobow.shopping.mappers.order.OrderResponseMapper;
import com.sobow.shopping.mappers.user.responses.UserAddressResponseMapper;
import com.sobow.shopping.mappers.user.responses.UserProfileResponseMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserProfileResponseMapperImpl implements UserProfileResponseMapper {
    
    private final UserAddressResponseMapper userAddressResponseMapper;
    private final CartResponseMapper cartResponseMapper;
    private final OrderResponseMapper orderResponseMapper;
    
    @Override
    public UserProfile mapToEntity(UserProfileResponse userProfileResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public UserProfileResponse mapToDto(UserProfile userProfile) {
        return UserProfileResponse.builder()
                                  .firstName(userProfile.getFirstName())
                                  .lastName(userProfile.getLastName())
                                  .userAddress(userAddressResponseMapper.mapToDto(userProfile.getAddress()))
                                  .cart(Optional.ofNullable(userProfile.getCart())
                                                .map(cartResponseMapper::mapToDto)
                                                .orElseGet(CartResponse::empty))
                                  .orders(userProfile.getOrders().stream().map(orderResponseMapper::mapToDto).toList())
                                  .build();
    }
}
