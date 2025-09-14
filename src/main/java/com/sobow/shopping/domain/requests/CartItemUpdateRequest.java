package com.sobow.shopping.domain.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemUpdateRequest(@NotNull @Positive Long cartItemId, @NotNull @Positive Integer requestedQty) {

}
