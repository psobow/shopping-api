package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.controllers.category.dto.CategoryRequest;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.CategoryRepository;
import com.sobow.shopping.exceptions.CategoryAlreadyExistsException;
import com.sobow.shopping.services.category.Impl.CategoryServiceImpl;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTests {
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private CategoryServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    
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
            Category category = fixtures.categoryEntity();
            ReflectionTestUtils.setField(category, "id", fixtures.categoryId());
            
            CategoryRequest patch = fixtures.withCategoryName("new name")
                                            .categoryRequest();
            
            when(categoryRepository.findById(fixtures.categoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameAndIdNot(patch.name(), fixtures.categoryId())).thenReturn(false);
            
            // When
            Category result = underTest.partialUpdateById(fixtures.categoryId(), patch);
            
            // Then
            // Assert: repository was queried for the entity
            verify(categoryRepository).findById(fixtures.categoryId());
            
            // Assert: uniqueness check performed with the new name
            verify(categoryRepository).existsByNameAndIdNot(patch.name(), fixtures.categoryId());
            
            // Assert: entity state was updated
            assertThat(patch.name()).isEqualTo(category.getName());
            
            // Assert: service returns the same (updated) entity instance
            assertThat(category).isSameAs(result);
        }
        
        @Test
        public void partialUpdateById_should_ThrowAlreadyExists_when_CategoryNameAlreadyExists() {
            // Given
            Category category = fixtures.categoryEntity();
            String nameBefore = category.getName();
            ReflectionTestUtils.setField(category, "id", fixtures.categoryId());
            
            CategoryRequest patch = fixtures.withCategoryName("name already exists")
                                            .categoryRequest();
            
            when(categoryRepository.findById(fixtures.categoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameAndIdNot(patch.name(), fixtures.categoryId())).thenReturn(true);
            
            // When & Then
            // Assert: throws when new name already exists
            assertThrows(CategoryAlreadyExistsException.class,
                         () -> underTest.partialUpdateById(fixtures.categoryId(), patch));
            
            // Assert: repository looked up the entity
            verify(categoryRepository).findById(fixtures.categoryId());
            
            // Assert: uniqueness check performed with patch name
            verify(categoryRepository).existsByNameAndIdNot(patch.name(), fixtures.categoryId());
            
            // Assert: save must NOT be called
            verify(categoryRepository, never()).save(any());
            
            // Assert: entity remains unchanged
            assertThat(category.getName()).isEqualTo(nameBefore);
        }
    }
    
    @Nested
    @DisplayName("findBy")
    class findBy {
        
        @Test
        void findById_should_ThrowNotFound_when_CategoryIdDoesNotExist() {
            when(categoryRepository.findById(fixtures.nonExistingId())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> underTest.findById(fixtures.nonExistingId()));
        }
    }
}
