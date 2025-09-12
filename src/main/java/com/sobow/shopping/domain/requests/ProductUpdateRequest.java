package com.sobow.shopping.domain.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductUpdateRequest(
    @Size(max = 120)
    @Pattern(regexp = ".*\\S.*",
        message = "Brand name must contain at least one non-whitespace character")
    String name,
    
    @Size(max = 120)
    @Pattern(regexp = ".*\\S.*",
        message = "Brand name must contain at least one non-whitespace character")
    String brandName,
    
    @DecimalMin(value = "0.0", inclusive = false)
    BigDecimal price,
    
    @PositiveOrZero
    Integer availableQuantity,
    
    @Size(max = 2000)
    @Pattern(regexp = ".*\\S.*",
        message = "Brand name must contain at least one non-whitespace character")
    String description,
    
    @Positive
    Long categoryId
) {

}
