package com.sobow.shopping.domain.user.requests.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateUserAuthoritiesRequest(
    @NotNull @Valid ListAuthorityDto authorities
) {

}
