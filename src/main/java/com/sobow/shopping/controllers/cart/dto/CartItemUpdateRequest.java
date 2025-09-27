package com.sobow.shopping.controllers.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CartItemUpdateRequest(
    @NotNull @PositiveOrZero Integer requestedQty
) {

}
