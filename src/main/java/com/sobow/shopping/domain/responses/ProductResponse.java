package com.sobow.shopping.domain.responses;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(Long id,
                              String name,
                              String brandName,
                              BigDecimal price,
                              Integer availableQuantity,
                              String description,
                              Long categoryId,
                              List<Long> imagesId) {
    
}
