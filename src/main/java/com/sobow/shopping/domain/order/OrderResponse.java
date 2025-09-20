package com.sobow.shopping.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderResponse(Long id,
                            String status,
                            LocalDateTime createdAt,
                            BigDecimal totalOrderPrice,
                            List<OrderItemResponse> itemResponseList) {
    
}
