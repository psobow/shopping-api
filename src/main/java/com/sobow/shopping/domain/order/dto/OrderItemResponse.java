package com.sobow.shopping.domain.order.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record OrderItemResponse(
    Long id,
    Long orderId,
    Integer requestedQty,
    String productName,
    String productBrandName,
    BigDecimal productPrice,
    BigDecimal totalItemPrice
) {
    
}
