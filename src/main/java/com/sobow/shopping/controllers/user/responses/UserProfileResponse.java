package com.sobow.shopping.controllers.user.responses;

import com.sobow.shopping.controllers.cart.dto.CartResponse;
import com.sobow.shopping.controllers.order.dto.OrderResponse;
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
