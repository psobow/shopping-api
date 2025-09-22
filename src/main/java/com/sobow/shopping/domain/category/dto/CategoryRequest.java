package com.sobow.shopping.domain.category.dto;

import static com.sobow.shopping.validation.ValidationUtils.normalizeSingleLine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank @Size(max = 120) String name
) {
    
    public CategoryRequest {
        name = normalizeSingleLine(name);
    }
}
