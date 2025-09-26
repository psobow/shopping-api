package com.sobow.shopping.services;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.dto.CategoryRequest;
import java.util.List;

public interface CategoryService {
    
    Category findById(long id);
    
    Category findByName(String name);
    
    List<Category> findAll();
    
    Category create(CategoryRequest request);
    
    void deleteById(long id);
    
    Category partialUpdateById(long id, CategoryRequest request);
}
