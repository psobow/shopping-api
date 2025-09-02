package com.sobow.shopping.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductUpdateRequest(
    @Size(max = 120) String name,
    @Size(max = 120) String brandName,
    @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
    @PositiveOrZero Integer availableQuantity,
    @Size(max = 2000) String description,
    @Positive Long categoryId
) {

}
