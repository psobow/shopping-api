package com.sobow.shopping.mappers.category.Impl;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.dto.CategoryResponse;
import com.sobow.shopping.mappers.category.CategoryResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryResponseMapperImpl implements CategoryResponseMapper {
    
    @Override
    public Category mapToEntity(CategoryResponse categoryResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public CategoryResponse mapToDto(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName());
    }
}
