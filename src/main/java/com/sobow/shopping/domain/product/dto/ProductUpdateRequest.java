package com.sobow.shopping.domain.product.dto;

import static com.sobow.shopping.validation.ValidationUtils.normalizeMultiLine;
import static com.sobow.shopping.validation.ValidationUtils.normalizePrice;
import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductUpdateRequest(
    @Size(min = 1, max = 120) String name,
    @Size(min = 1, max = 120) String brandName,
    @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
    @PositiveOrZero Integer availableQuantity,
    @Size(min = 1, max = 2000) String description,
    @Positive Long categoryId
) {
    
    public ProductUpdateRequest {
        name = normalizeSingleLine(name);
        brandName = normalizeSingleLine(brandName);
        description = normalizeMultiLine(description);
        price = normalizePrice(price);
    }
}
