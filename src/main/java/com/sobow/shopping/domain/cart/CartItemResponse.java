package com.sobow.shopping.domain.cart;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CartItemResponse(Long id, Long productId, Long cartId, Integer requestedQty, BigDecimal totalItemPrice) {

}
