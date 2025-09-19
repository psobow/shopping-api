package com.sobow.shopping.domain.product;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record ProductResponse(Long id,
                              String name,
                              String brandName,
                              BigDecimal price,
                              Integer availableQty,
                              String description,
                              Long categoryId,
                              List<Long> imagesId) {
    
}
