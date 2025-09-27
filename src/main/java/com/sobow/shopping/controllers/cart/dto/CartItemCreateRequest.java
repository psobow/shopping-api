package com.sobow.shopping.controllers.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemCreateRequest(
    @NotNull @Positive Long productId,
    @NotNull @Positive Integer requestedQty
) {

}
