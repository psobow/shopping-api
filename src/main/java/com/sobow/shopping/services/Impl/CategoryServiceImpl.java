package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Category save(Category category) {
        assertCategoryUnique(category.getName());
        return categoryRepository.save(category);
    }
    
    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
            "Category with " + id + " not found"));
    }
    
    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException(
            "Category with " + name + " not found"));
    }
    
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    
    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }
    
    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
    
    @Transactional
    @Override
    public Category partialUpdateById(Category patch, Long existingId) {
        Category existingCategory = findById(existingId);
        
        if (patch.getName() != null && existingCategory.getName() != patch.getName()) {
            assertCategoryUnique(patch.getName());
            existingCategory.setName(patch.getName());
        }
        return categoryRepository.save(existingCategory);
    }
    
    private void assertCategoryUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalStateException("Category with name \"%s\" already exists".formatted(name));
        }
    }
}
