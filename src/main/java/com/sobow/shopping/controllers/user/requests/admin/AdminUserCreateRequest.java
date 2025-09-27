package com.sobow.shopping.controllers.user.requests.admin;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.controllers.user.requests.PasswordRequest;
import com.sobow.shopping.controllers.user.requests.UserProfileCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminUserCreateRequest(
    @NotBlank @Email String email,
    @NotNull @Valid PasswordRequest password,
    @NotNull @Valid UserProfileCreateRequest userProfile,
    @NotNull @Valid UserAuthoritiesRequest authorities
) {
    
    public AdminUserCreateRequest {
        email = normalizeEmail(email);
    }
}
