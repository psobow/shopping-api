package com.sobow.shopping.domain.user.requests.admin;

import com.sobow.shopping.domain.user.requests.dto.AuthoritiesDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateUserAuthorityRequest(
    @NotBlank @Email String email,
    @NotNull @Valid AuthoritiesDto authorities
) {

}
