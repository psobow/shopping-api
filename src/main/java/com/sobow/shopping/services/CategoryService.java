package com.sobow.shopping.services;

import com.sobow.shopping.domain.Category;
import java.util.List;

public interface CategoryService {
    
    Category save(Category category);
    
    Category findById(Long id);
    
    Category findByName(String name);
    
    List<Category> findAll();
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByName(String name);
    
    Category partialUpdateById(Category patch, Long existingId);
}
