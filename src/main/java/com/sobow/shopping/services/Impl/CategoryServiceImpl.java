package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.exceptions.CategoryAlreadyExistsException;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.services.CategoryService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
        String name = normalize(category.getName());
        assertCategoryUnique(name, null);
        
        category.setName(name);
        return categoryRepository.save(category);
    }
    
    @Transactional
    @Override
    public Category partialUpdateById(Category patch, long id) {
        Category existingCategory = findById(id);
        String patchName = normalize(patch.getName());
        
        assertCategoryUnique(patchName, existingCategory.getId());
        existingCategory.setName(patchName);
        
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
    
    private void assertCategoryUnique(String name, @Nullable Long existingCategoryId) {
        boolean duplicate =
            (existingCategoryId == null && categoryRepository.existsByName(name)) ||
                (existingCategoryId != null && categoryRepository.existsByNameAndIdNot(name, existingCategoryId));
        
        if (duplicate) {
            throw new CategoryAlreadyExistsException(name);
        }
    }
    
    private String normalize(String s) {
        return Objects.requireNonNull(s, "value required").strip().toLowerCase(Locale.ROOT);
    }
}
