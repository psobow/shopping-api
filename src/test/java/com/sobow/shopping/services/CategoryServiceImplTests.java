package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.exceptions.ResourceAlreadyExistsException;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.services.Impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    
    private final static long EXISTING_ID = 1L;
    private final static long NON_EXISTING_ID = 999L;
    
    private Category getCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }
    
    @Test
    void findById_should_ThrowNotFound_when_CategoryIdDoesNotExist() {
        when(categoryRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> underTest.findById(NON_EXISTING_ID));
    }
    
    @Nested
    @DisplayName("save")
    class save {
        
        @Test
        public void save_should_ReturnSavedCategory_when_ValidInput() {
            // Given
            Category category = getCategory("Unique");
            
            when(categoryRepository.existsByName(category.getName())).thenReturn(false);
            when(categoryRepository.save(category)).thenReturn(category);
            
            // When
            Category saved = underTest.save(category);
            
            // Then
            assertSame(category, saved);
            verify(categoryRepository).existsByName(category.getName());
            verify(categoryRepository).save(category);
        }
        
        @Test
        public void save_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            Category category = getCategory("Name Already Exists");
            
            when(categoryRepository.existsByName(category.getName())).thenReturn(true);
            
            // When & Then
            assertThrows(ResourceAlreadyExistsException.class, () -> underTest.save(category));
            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("partialUpdateById")
    class partialUpdateById {
        
        @Test
        public void partialUpdateById_should_ReturnUpdatedCategory_when_ValidInput() {
            // Given
            Category existing = getCategory("old");
            Category patch = getCategory("new");
            
            when(categoryRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existing));
            when(categoryRepository.existsByName(patch.getName())).thenReturn(false);
            when(categoryRepository.save(existing)).thenReturn(existing);
            
            // When
            Category result = underTest.partialUpdateById(patch, EXISTING_ID);
            
            // Then
            assertEquals(patch.getName(), existing.getName());
            assertSame(existing, result);
            verify(categoryRepository).existsByName(patch.getName());
            verify(categoryRepository).save(existing);
        }
        
        @Test
        public void partialUpdateById_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            Category existing = getCategory("old");
            Category patch = getCategory("Name Already Exists");
            
            when(categoryRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existing));
            when(categoryRepository.existsByName(patch.getName())).thenReturn(true);
            
            // When & Then
            assertThrows(ResourceAlreadyExistsException.class, () -> underTest.partialUpdateById(patch, EXISTING_ID));
            verify(categoryRepository, never()).save(any());
        }
        
        @Test
        public void partialUpdateById_should_NotPersist_when_NoChangesDetected() {
            // Given
            Category existing = getCategory("name");
            Category patch = getCategory("name");
            
            when(categoryRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existing));
            
            // When
            Category result = underTest.partialUpdateById(patch, EXISTING_ID);
            
            // Then
            verify(categoryRepository, never()).save(any());
            assertSame(existing, result);
        }
    }
    
}
