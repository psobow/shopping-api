package com.sobow.shopping.mappers;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.requests.CategoryCreateRequest;

public class CategoryRequestMapper implements Mapper<Category, CategoryCreateRequest> {
    
    @Override
    public Category mapToEntity(CategoryCreateRequest categoryCreateRequest) {
        return new Category().builder()
                             .name(categoryCreateRequest.name())
                             .build();
    }
    
    @Override
    public CategoryCreateRequest mapToDto(Category category) {
        return null;
    }
}
