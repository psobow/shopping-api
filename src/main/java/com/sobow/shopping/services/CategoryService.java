package com.sobow.shopping.services;

import com.sobow.shopping.domain.category.Category;
import java.util.List;

public interface CategoryService {
    
    Category create(Category category);
    
    Category findById(long id);
    
    Category findByName(String name);
    
    List<Category> findAll();
    
    void deleteById(long id);
    
    boolean existsById(long id);
    
    boolean existsByName(String name);
    
    Category partialUpdateById(Category patch, long id);
}
