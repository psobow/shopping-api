package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.entities.Category;
import com.sobow.shopping.domain.requests.CategoryRequest;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryRequestMapper implements Mapper<Category, CategoryRequest> {
    
    @Override
    public Category mapToEntity(CategoryRequest categoryRequest) {
        return new Category().builder()
                             .name(categoryRequest.name())
                             .build();
    }
    
    @Override
    public CategoryRequest mapToDto(Category category) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
}
