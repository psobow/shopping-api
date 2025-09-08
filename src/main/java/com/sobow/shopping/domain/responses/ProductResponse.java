package com.sobow.shopping.domain.responses;

import java.math.BigDecimal;

public record ProductResponse(Long id,
                              String name,
                              String brandName,
                              BigDecimal price,
                              Integer availableQuantity,
                              String description,
                              Long categoryId) {
    
}
