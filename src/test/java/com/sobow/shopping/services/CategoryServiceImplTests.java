package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.exceptions.CategoryAlreadyExistsException;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.services.Impl.CategoryServiceImpl;
import com.sobow.shopping.utils.TestFixtures;
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
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Test
    void findById_should_ThrowNotFound_when_CategoryIdDoesNotExist() {
        when(categoryRepository.findById(fixtures.nonExistingId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> underTest.findById(fixtures.nonExistingId()));
    }
    
    @Nested
    @DisplayName("create")
    class create {
        
        @Test
        public void create_should_ReturnSavedCategory_when_ValidInput() {
            // Given
            Category category = fixtures.categoryEntity();
            
            when(categoryRepository.existsByName(category.getName())).thenReturn(false);
            when(categoryRepository.save(category)).thenReturn(category);
            
            // When
            Category result = underTest.create(category);
            
            // Then
            assertSame(category, result);
        }
        
        @Test
        public void create_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            Category category = fixtures.withCategoryName("name already exists")
                                        .categoryEntity();
            
            when(categoryRepository.existsByName(category.getName())).thenReturn(true);
            
            // When & Then
            assertThrows(CategoryAlreadyExistsException.class, () -> underTest.create(category));
            verify(categoryRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("partialUpdateById")
    class partialUpdateById {
        
        @Test
        public void partialUpdateById_should_ReturnUpdatedCategory_when_ValidInput() {
            // Given
            Category category = fixtures.withCategoryName("old name")
                                        .categoryEntity();
            
            Category patch = fixtures.withCategoryName("new name")
                                     .categoryEntity();
            
            when(categoryRepository.findById(fixtures.categoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName(patch.getName())).thenReturn(false);
            
            // When
            Category result = underTest.partialUpdateById(patch, fixtures.categoryId());
            
            // Then
            assertSame(category, result);
            assertEquals(patch.getName(), category.getName());
        }
        
        @Test
        public void partialUpdateById_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            Category category = fixtures.withCategoryName("old name")
                                        .categoryEntity();
            
            Category patch = fixtures.withCategoryName("name already exists")
                                     .categoryEntity();
            
            when(categoryRepository.findById(fixtures.categoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName(patch.getName())).thenReturn(true);
            
            // When & Then
            assertThrows(CategoryAlreadyExistsException.class, () -> underTest.partialUpdateById(patch, fixtures.categoryId()));
        }
        
        @Test
        public void partialUpdateById_should_NotPersist_when_NoChangesDetected() {
            // Given
            Category category = fixtures.categoryEntity();
            Category patchWithSameName = fixtures.categoryEntity();
            
            when(categoryRepository.findById(fixtures.categoryId())).thenReturn(Optional.of(category));
            
            // When
            Category result = underTest.partialUpdateById(patchWithSameName, fixtures.categoryId());
            
            // Then
            assertSame(category, result);
        }
    }
}
