package com.sobow.shopping.controllers.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record CartResponse(
    Long id,
    BigDecimal totalCartPrice,
    List<CartItemResponse> cartItems
) {
    
    public static CartResponse empty() {
        return CartResponse.builder()
                           .id(null)
                           .totalCartPrice(BigDecimal.ZERO)
                           .cartItems(List.of())
                           .build();
    }
}
