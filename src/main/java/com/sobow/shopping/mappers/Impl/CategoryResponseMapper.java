package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.entities.Category;
import com.sobow.shopping.domain.responses.CategoryResponse;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryResponseMapper implements Mapper<Category, CategoryResponse> {
    
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
