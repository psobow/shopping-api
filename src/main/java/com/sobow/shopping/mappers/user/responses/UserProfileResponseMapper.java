package com.sobow.shopping.mappers.user.responses;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.dto.CartResponse;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.order.dto.OrderResponse;
import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.responses.UserAddressResponse;
import com.sobow.shopping.domain.user.responses.UserProfileResponse;
import com.sobow.shopping.mappers.Mapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserProfileResponseMapper implements Mapper<UserProfile, UserProfileResponse> {
    
    private final Mapper<UserAddress, UserAddressResponse> userAddressResponseMapper;
    private final Mapper<Cart, CartResponse> cartResponseMapper;
    private final Mapper<Order, OrderResponse> orderResponseMapper;
    
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
