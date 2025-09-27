package com.sobow.shopping.controllers.user.requests.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AdminUserAuthoritiesUpdateRequest(
    @NotNull @Valid UserAuthoritiesRequest authorities
) {

}
