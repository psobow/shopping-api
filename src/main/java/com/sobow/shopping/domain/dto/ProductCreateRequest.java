package com.sobow.shopping.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductCreateRequest(
    @NotBlank @Size(max = 120) String name,
    @NotBlank @Size(max = 120) String brandName,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
    @NotNull @PositiveOrZero Integer availableQuantity,
    @Size(max = 2000) String description,
    @NotNull @Positive Long categoryId
) {

}
