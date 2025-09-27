package com.sobow.shopping.controllers.cart.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CartItemResponse(
    Long id,
    Long productId,
    Long cartId,
    Integer requestedQty,
    BigDecimal productPrice,
    BigDecimal totalItemPrice
) {

}
