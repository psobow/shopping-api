package com.sobow.shopping.domain.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    Long id,
    BigDecimal totalCartPrice,
    List<CartItemResponse> itemResponseList
) {

}
