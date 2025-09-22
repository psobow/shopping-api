package com.sobow.shopping.domain.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CartItemUpdateRequest(
    @NotNull @PositiveOrZero Integer requestedQty
) {

}
