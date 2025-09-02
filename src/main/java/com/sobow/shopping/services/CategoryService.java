package com.sobow.shopping.services;

import com.sobow.shopping.domain.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    
    Category save(Category category);
    
    Optional<Category> findById(Long id);
    
    Optional<Category> findByName(String name);
    
    List<Category> findAll();
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByName(String name);
    
    Category partialUpdateById(Category category, Long id);
}
