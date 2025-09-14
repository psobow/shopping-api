package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Category;
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
        when(categoryRepository.findById(fixtures.getNonExistingId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> underTest.findById(fixtures.getNonExistingId()));
    }
    
    @Nested
    @DisplayName("save")
    class save {
        
        @Test
        public void save_should_ReturnSavedCategory_when_ValidInput() {
            // Given
            Category category = fixtures.withCategoryId(null)
                                        .withCategoryEmptyProducts()
                                        .categoryEntity();
            
            when(categoryRepository.existsByName(category.getName())).thenReturn(false);
            when(categoryRepository.save(category)).thenAnswer(inv -> {
                Category c = inv.getArgument(0);
                c.setId(1L);
                return c;
            });
            
            // When
            Category result = underTest.save(category);
            
            // Then
            assertSame(category, result);
            assertEquals(1L, result.getId());
            
            verify(categoryRepository).existsByName(category.getName());
            verify(categoryRepository).save(category);
        }
        
        @Test
        public void save_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            Category category = fixtures.withCategoryId(null)
                                        .withCategoryName("name already exists")
                                        .withCategoryEmptyProducts()
                                        .categoryEntity();
            
            when(categoryRepository.existsByName(category.getName())).thenReturn(true);
            
            // When & Then
            assertThrows(CategoryAlreadyExistsException.class, () -> underTest.save(category));
            verify(categoryRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("partialUpdateById")
    class partialUpdateById {
        
        @Test
        public void partialUpdateById_should_ReturnUpdatedCategory_when_ValidInput() {
            // Given
            Category existing = fixtures.withCategoryName("old name")
                                        .categoryEntity();
            Long existingId = existing.getId();
            
            Category patch = fixtures.withCategoryId(null)
                                     .withCategoryName("new name")
                                     .withCategoryEmptyProducts()
                                     .categoryEntity();
            
            when(categoryRepository.findById(existingId)).thenReturn(Optional.of(existing));
            when(categoryRepository.existsByName(patch.getName())).thenReturn(false);
            when(categoryRepository.save(existing)).thenAnswer(inv -> {
                Category c = inv.getArgument(0);
                c.setName(patch.getName());
                return c;
            });
            
            // When
            Category result = underTest.partialUpdateById(patch, existingId);
            
            // Then
            assertEquals(patch.getName(), existing.getName());
            assertSame(existing, result);
            verify(categoryRepository).existsByName(patch.getName());
            verify(categoryRepository).save(existing);
        }
        
        @Test
        public void partialUpdateById_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            Category existing = fixtures.withCategoryName("old name")
                                        .categoryEntity();
            Long existingId = existing.getId();
            
            Category patch = fixtures.withCategoryId(null)
                                     .withCategoryName("name already exists")
                                     .withCategoryEmptyProducts()
                                     .categoryEntity();
            
            when(categoryRepository.findById(existingId)).thenReturn(Optional.of(existing));
            when(categoryRepository.existsByName(patch.getName())).thenReturn(true);
            
            // When & Then
            assertThrows(CategoryAlreadyExistsException.class, () -> underTest.partialUpdateById(patch, existingId));
            verify(categoryRepository, never()).save(any());
        }
        
        @Test
        public void partialUpdateById_should_NotPersist_when_NoChangesDetected() {
            // Given
            Category existing = fixtures.categoryEntity();
            Long existingId = existing.getId();
            Category patchWithSameName = fixtures.withCategoryId(null)
                                                 .withCategoryEmptyProducts()
                                                 .categoryEntity();
            
            when(categoryRepository.findById(existingId)).thenReturn(Optional.of(existing));
            
            // When
            Category result = underTest.partialUpdateById(patchWithSameName, existingId);
            
            // Then
            assertSame(existing, result);
            verify(categoryRepository, never()).save(any());
        }
    }
}
