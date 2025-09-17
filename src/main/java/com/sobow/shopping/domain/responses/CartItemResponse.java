package com.sobow.shopping.domain.responses;

import java.math.BigDecimal;

public record CartItemResponse(Long id, Long productId, Long cartId, Integer requestedQty, BigDecimal totalItemPrice) {

}
