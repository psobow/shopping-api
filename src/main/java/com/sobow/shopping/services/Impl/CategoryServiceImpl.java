package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.exceptions.CategoryAlreadyExistsException;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional
    @Override
    public Category create(Category category) {
        assertCategoryUnique(category.getName());
        return categoryRepository.save(category);
    }
    
    @Transactional
    @Override
    public Category partialUpdateById(Category patch, long id) {
        Category existingCategory = findById(id);
        
        if (patch.getName() != null && existingCategory.getName() != patch.getName()) {
            assertCategoryUnique(patch.getName());
            existingCategory.setName(patch.getName());
        }
        return existingCategory;
    }
    
    @Override
    public Category findById(long id) {
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
    public void deleteById(long id) {
        categoryRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(long id) {
        return categoryRepository.existsById(id);
    }
    
    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
    
    private void assertCategoryUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new CategoryAlreadyExistsException(name);
        }
    }
}
