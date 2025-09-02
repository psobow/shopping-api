package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.services.CategoryService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Category save(Category category) {
        assertCategoryUnique(category.getName(), null);
        return categoryRepository.save(category);
    }
    
    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
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
    
    @Override
    public Category partialUpdateById(Category category, Long id) {
        Category existingCategory = categoryRepository.findById(id)
                                                      .orElseThrow(() -> new EntityNotFoundException(
                                                          "Category with " + id + " not found"));
        
        if (category.getName() != null) {
            assertCategoryUnique(category.getName(), id);
            existingCategory.setName(category.getName());
        }
        return categoryRepository.save(existingCategory);
    }
    
    private void assertCategoryUnique(String name, @Nullable Long excludeId) {
        if (name == null) return;
        boolean taken = (excludeId == null)
                        ? categoryRepository.existsByName(name)
                        : categoryRepository.existsByNameAndIdNot(name, excludeId); // Check if there is any category with the given name and with an id that is different from the one we are updating.
        
        if (taken) {
            throw new IllegalStateException("Category with name \"%s\" already exists".formatted(name));
        }
    }
}
