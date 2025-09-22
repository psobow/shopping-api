package com.sobow.shopping.services;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.dto.CategoryRequest;
import java.util.List;

public interface CategoryService {
    
    Category create(CategoryRequest request);
    
    Category findById(long id);
    
    Category findByName(String name);
    
    List<Category> findAll();
    
    void deleteById(long id);
    
    boolean existsById(long id);
    
    boolean existsByName(String name);
    
    Category partialUpdateById(long id, CategoryRequest request);
}
