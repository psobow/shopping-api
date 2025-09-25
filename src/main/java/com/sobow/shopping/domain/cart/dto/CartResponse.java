package com.sobow.shopping.domain.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record CartResponse(
    Long id,
    BigDecimal totalCartPrice,
    List<CartItemResponse> itemResponseList
) {
    
    public static CartResponse empty() {
        return CartResponse.builder()
                           .id(null)
                           .totalCartPrice(BigDecimal.ZERO)
                           .itemResponseList(List.of())
                           .build();
    }
}
