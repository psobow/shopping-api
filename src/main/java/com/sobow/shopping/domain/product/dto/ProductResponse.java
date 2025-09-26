package com.sobow.shopping.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record ProductResponse(
    Long id,
    String name,
    String brandName,
    BigDecimal price,
    Integer availableQty,
    String description,
    Long categoryId,
    @JsonInclude(Include.NON_NULL) List<Long> imagesId
) {
    
}
