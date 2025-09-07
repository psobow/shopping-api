package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.Impl.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private Mapper<Product, ProductCreateRequest> productRequestMapper;
    
    @InjectMocks
    private ProductServiceImpl underTest;
    
    private final static long productExistingId = 1L;
    private final static long categoryExistingId = 2L;
    private final static long nonExistingId = 999L;
    
    @Nested
    @DisplayName("save")
    class save {
        
        @Test
        public void save_should_ReturnSavedProduct_when_Valid() {
            // Given
            ProductCreateRequest dto = new ProductCreateRequest(
                "ProductName", "Brand", new BigDecimal("10.00"), 5, "Desc", productExistingId);
            
            Product mapped = new Product();       // what mapper returns
            Category category = new Category();   // what repo returns
            
            when(productRequestMapper.mapToEntity(dto)).thenReturn(mapped);
            when(categoryRepository.findById(productExistingId)).thenReturn(Optional.of(category));
            when(productRepository.save(mapped)).thenReturn(mapped);
            // When
            Product result = underTest.save(dto);
            
            // Then
            assertSame(mapped, result);                // we got back what repository returned
            assertSame(category, mapped.getCategory()); // category was set on the entity
        }
        
        @Test
        public void save_should_ThrowNotFound_when_CategoryMissing() {
            // Given
            ProductCreateRequest dto = new ProductCreateRequest(
                "ProductName", "Brand", new BigDecimal("10.00"), 5, "Desc", nonExistingId);
            when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());
            
            // When + Then
            assertThrows(EntityNotFoundException.class, () -> underTest.save(dto));
            verify(productRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("partialUpdateById")
    class partialUpdateById {
        
        @Test
        public void partialUpdateById_should_ReturnUpdatedProduct_when_ValidPatch() {
            // Given
            Product product = new Product();
            Category category = new Category();
            category.setName("newCategoryName");
            
            when(productRepository.findById(productExistingId)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(categoryExistingId)).thenReturn(Optional.of(category));
            when(productRepository.save(product)).thenReturn(product);
            
            // When
            ProductUpdateRequest patch =
                new ProductUpdateRequest("newProductName", null, null, null, null, categoryExistingId);
            Product result = underTest.partialUpdateById(patch, productExistingId);
            
            // Then
            assertSame(product, result);
            assertEquals("newProductName", result.getName());
            assertEquals("newCategoryName", result.getCategory().getName());
            
            // Interactions with mock
            verify(productRepository).findById(productExistingId);
            verify(productRepository).save(product);
            verifyNoMoreInteractions(productRepository);
        }
        
        @Test
        public void partialUpdateById_should_ThrowNotFound_when_CategoryIdDoesNotExist() {
            // Given
            Product product = new Product();
            ProductUpdateRequest patch =
                new ProductUpdateRequest(null, null, null, null, null, nonExistingId);
            
            when(productRepository.findById(productExistingId)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());
            
            // When + Then
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.partialUpdateById(patch, productExistingId));
            verify(productRepository, never()).save(any());
        }
    }
}
