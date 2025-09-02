package com.sobow.shopping.domain.dto;

import java.math.BigDecimal;

public record ProductRequest(String name,
                             String brandName,
                             BigDecimal price,
                             Integer availableQuantity,
                             String description,
                             Long categoryId) {
    
}
