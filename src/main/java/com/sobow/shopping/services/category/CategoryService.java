package com.sobow.shopping.services.category;

import com.sobow.shopping.controllers.category.dto.CategoryRequest;
import com.sobow.shopping.domain.category.Category;
import java.util.List;

public interface CategoryService {
    
    Category findById(long id);
    
    Category findByName(String name);
    
    List<Category> findAll();
    
    Category create(CategoryRequest request);
    
    void deleteById(long id);
    
    Category partialUpdateById(long id, CategoryRequest request);
}
