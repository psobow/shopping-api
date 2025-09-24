package com.sobow.shopping.domain.user.requests.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sobow.shopping.validation.annotations.Distinct;
import com.sobow.shopping.validation.annotations.ValidRoles;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ListAuthorityDto(
    @Distinct
    @ValidRoles
    @NotEmpty
    @Size(max = 10)
    @Valid
    List<AuthorityDto> value
) {
    
    @JsonCreator(mode = Mode.DELEGATING)
    public ListAuthorityDto(List<AuthorityDto> value) {
        this.value = value;
    }
    
    @JsonValue
    public List<AuthorityDto> json() {
        return value;
    }
}
