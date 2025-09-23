package com.sobow.shopping.domain.user.requests;

import static com.sobow.shopping.validation.ValidationUtils.normalizeEmail;

import com.sobow.shopping.validation.annotations.Distinct;
import com.sobow.shopping.validation.annotations.ValidRoles;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UserCreateRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6, max = 100) String password,
    @NotNull @Valid UserProfileCreateRequest userProfile,
    @Distinct @ValidRoles @NotEmpty @Size(max = 10) List<UserAuthorityRequest> authorities
) {
    
    public UserCreateRequest {
        email = normalizeEmail(email);
    }
}
