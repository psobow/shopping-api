package com.sobow.shopping.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductUpdateRequest(
    @Pattern(regexp = ".*\\S.*", message = "Name must contain at least one non-whitespace character")
    @Size(max = 120) String name,
    
    @Pattern(regexp = ".*\\S.*", message = "Brand name must contain at least one non-whitespace character")
    @Size(max = 120) String brandName,
    
    @Pattern(regexp = ".*\\S.*", message = "Description name must contain at least one non-whitespace character")
    @Size(max = 2000) String description,
    
    @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
    @PositiveOrZero Integer availableQuantity,
    
    @Positive Long categoryId
) {

}
