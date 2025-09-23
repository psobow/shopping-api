package com.sobow.shopping.domain.user.responses;

import com.sobow.shopping.domain.cart.dto.CartResponse;
import com.sobow.shopping.domain.order.dto.OrderResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record UserProfileResponse(
    String firstName,
    String lastName,
    UserAddressResponse userAddress,
    CartResponse cart,
    List<OrderResponse> orders
) {

}
