package com.sobow.shopping.domain.requests;

import com.sobow.shopping.domain.requests.markers.Create;
import com.sobow.shopping.domain.requests.markers.Update;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(groups = Create.class)
    @Size(max = 120, groups = {Create.class, Update.class})
    @Pattern(regexp = ".*\\S.*",
        message = "Brand name must contain at least one non-whitespace character",
        groups = Update.class)
    String name,
    
    @NotBlank(groups = Create.class)
    @Size(max = 120, groups = {Create.class, Update.class})
    @Pattern(regexp = ".*\\S.*",
        message = "Brand name must contain at least one non-whitespace character",
        groups = Update.class)
    String brandName,
    
    @NotNull(groups = Create.class)
    @DecimalMin(value = "0.0", inclusive = false, groups = {Create.class, Update.class})
    BigDecimal price,
    
    @NotNull(groups = Create.class)
    @PositiveOrZero(groups = {Create.class, Update.class})
    Integer availableQuantity,
    
    @NotBlank(groups = Create.class)
    @Size(max = 2000, groups = {Create.class, Update.class})
    @Pattern(regexp = ".*\\S.*",
        message = "Brand name must contain at least one non-whitespace character",
        groups = Update.class)
    String description,
    
    @NotNull(groups = Create.class)
    @Positive(groups = {Create.class, Update.class})
    Long categoryId
) {

}
