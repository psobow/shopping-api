package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.services.Impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTests {
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private CategoryServiceImpl underTest;
    
    @Test
    public void save_Success() {
        // Given
        Category category = new Category();
        String uniqueName = "Unique";
        category.setName(uniqueName);
        when(categoryRepository.existsByName(uniqueName)).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        
        // When
        Category saved = underTest.save(category);
        
        // Then
        assertSame(category, saved);
        verify(categoryRepository).existsByName(uniqueName);
        verify(categoryRepository).save(category);
    }
    
    @Test
    public void save_Duplicate_Throws() {
        // Given
        Category category = new Category();
        String duplicate = "Duplicate";
        category.setName(duplicate);
        when(categoryRepository.existsByName(duplicate)).thenReturn(true);
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> underTest.save(category));
        verify(categoryRepository, never()).save(any());
    }
    
    @Test
    public void partialUpdateById_Success() {
        // Given
        Category existing = new Category();
        existing.setName("old");
        Long existingId = 10L;
        
        Category patch = new Category();
        String newName = "new";
        patch.setName(newName);
        
        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName(newName)).thenReturn(false);
        when(categoryRepository.save(existing)).thenReturn(existing);
        
        // When
        Category result = underTest.partialUpdateById(patch, existingId);
        
        // Then
        assertEquals(newName, existing.getName());
        assertSame(existing, result);
        verify(categoryRepository).existsByName(newName);
        verify(categoryRepository).save(existing);
    }
    
    @Test
    public void partialUpdateById_Duplicate_Throws() {
        // Given
        Category existing = new Category();
        existing.setName("old");
        Long existingId = 10L;
        
        Category patch = new Category();
        String newName = "nameAlreadyTakenByOtherCategory";
        patch.setName(newName);
        
        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName(newName)).thenReturn(true);
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> underTest.partialUpdateById(patch, existingId));
        verify(categoryRepository, never()).save(any());
    }
    
    @Test
    void findById_NonExistingId_Throws() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> underTest.findById(99L));
    }
}
