package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.dto.CategoryRequest;
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
            CategoryRequest request = fixtures.categoryRequest();
            Category category = fixtures.categoryEntity();
            
            when(categoryRepository.existsByName(request.name())).thenReturn(false);
            when(categoryRepository.save(any())).thenReturn(category);
            
            // When
            Category result = underTest.create(request);
            
            // Then
            // Assert: Uniqueness check
            verify(categoryRepository).existsByName(request.name());
            // Assert: Entity created
            verify(categoryRepository).save(any());
        }
        
        @Test
        public void create_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            CategoryRequest request = fixtures.withCategoryName("name already exists")
                                              .categoryRequest();
            
            when(categoryRepository.existsByName(request.name())).thenReturn(true);
            
            // When & Then
            // Assert: service throws when name already exists
            assertThrows(CategoryAlreadyExistsException.class, () -> underTest.create(request));
            
            // Assert: uniqueness check was performed with the request name
            verify(categoryRepository).existsByName(request.name());
            
            // Assert: repository.save(...) was NOT called
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
            
            CategoryRequest patch = fixtures.withCategoryName("new name")
                                            .categoryRequest();
            
            when(categoryRepository.findById(fixtures.categoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName(patch.name())).thenReturn(false);
            
            // When
            Category result = underTest.partialUpdateById(fixtures.categoryId(), patch);
            
            // Then
            // Assert: repository was queried for the entity
            verify(categoryRepository).findById(fixtures.categoryId());
            
            // Assert: uniqueness check performed with the new name
            verify(categoryRepository).existsByName(patch.name());
            
            // Assert: entity state was updated
            assertEquals(patch.name(), category.getName());
            
            // Assert: service returns the same (updated) entity instance
            assertSame(category, result);
        }
        
        @Test
        public void partialUpdateById_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            Category category = fixtures.withCategoryName("old name")
                                        .categoryEntity();
            
            CategoryRequest patch = fixtures.withCategoryName("name already exists")
                                            .categoryRequest();
            
            when(categoryRepository.findById(fixtures.categoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName(patch.name())).thenReturn(true);
            
            // When & Then
            // Assert: throws when new name already exists
            assertThrows(CategoryAlreadyExistsException.class,
                         () -> underTest.partialUpdateById(fixtures.categoryId(), patch));
            
            // Assert: repository looked up the entity
            verify(categoryRepository).findById(fixtures.categoryId());
            
            // Assert: uniqueness check performed with patch name
            verify(categoryRepository).existsByName(patch.name());
            
            // Assert: save must NOT be called
            verify(categoryRepository, never()).save(any());
            
            // Assert: entity remains unchanged
            assertEquals("old name", category.getName());
        }
    }
}
