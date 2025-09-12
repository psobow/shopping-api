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
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.Impl.ProductServiceImpl;
import com.sobow.shopping.utils.ProductFixtures;
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
public class ProductServiceImplTests {
    
    @Mock
    private ProductRepository productRepository;
    
    private final static long PRODUCT_EXISTING_ID = 1L;
    
    private ProductFixtures productFixtures = ProductFixtures.defaults();
    
    @Mock
    private Mapper<Product, ProductCreateRequest> productRequestMapper;
    
    @InjectMocks
    private ProductServiceImpl underTest;
    private final static long CATEGORY_EXISTING_ID = 2L;
    private final static long CATEGORY_NON_EXISTING_ID = 999L;
    @Mock
    private CategoryService categoryService;
    
    @Nested
    @DisplayName("save")
    class save {
        
        @Test
        public void save_should_ReturnSavedProduct_when_ValidInput() {
            // Given
            productFixtures.withCategoryId(CATEGORY_EXISTING_ID);
            ProductCreateRequest dto = productFixtures.getNewCreateRequest();
            
            Product mapped = new Product();       // what mapper returns
            Category category = new Category();   // what repo returns
            
            when(productRequestMapper.mapToEntity(dto)).thenReturn(mapped);
            when(categoryService.findById(CATEGORY_EXISTING_ID)).thenReturn(category);
            when(productRepository.save(mapped)).thenReturn(mapped);
            // When
            Product result = underTest.save(dto);
            
            // Then
            assertSame(mapped, result);                // we got back what repository returned
            assertSame(category, mapped.getCategory()); // category was set on the entity
        }
        
        @Test
        public void save_should_ThrowNotFound_when_CategoryIdDoesNotExists() {
            // Given
            productFixtures.withCategoryId(CATEGORY_NON_EXISTING_ID);
            ProductCreateRequest dto = productFixtures.getNewCreateRequest();
            when(categoryService.findById(CATEGORY_NON_EXISTING_ID)).thenThrow(new EntityNotFoundException());
            
            // When + Then
            assertThrows(EntityNotFoundException.class, () -> underTest.save(dto));
            verify(productRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("partialUpdateById")
    class partialUpdateById {
        
        @Test
        public void partialUpdateById_should_ReturnUpdatedProduct_when_ValidInput() {
            // Given
            productFixtures.withCategoryName("newCategoryName")
                           .withCategoryId(CATEGORY_EXISTING_ID)
                           .withProductName("newProductName");
            
            Product product = productFixtures.getNewEntity();
            Category category = product.getCategory();
            ProductUpdateRequest patch = productFixtures.getNewUpdateRequest();
            
            when(productRepository.findById(PRODUCT_EXISTING_ID)).thenReturn(Optional.of(product));
            when(categoryService.findById(CATEGORY_EXISTING_ID)).thenReturn(category);
            when(productRepository.save(product)).thenReturn(product);
            
            // When
            Product result = underTest.partialUpdateById(patch, PRODUCT_EXISTING_ID);
            
            // Then
            assertSame(product, result);
            assertEquals("newProductName", result.getName());
            assertEquals("newCategoryName", result.getCategory().getName());
            
            // Interactions with mock
            verify(productRepository).findById(PRODUCT_EXISTING_ID);
            verify(productRepository).save(product);
            verifyNoMoreInteractions(productRepository);
        }
        
        @Test
        public void partialUpdateById_should_ThrowNotFound_when_CategoryIdDoesNotExist() {
            // Given
            productFixtures.withCategoryId(CATEGORY_NON_EXISTING_ID);
            Product product = productFixtures.getNewEntity();
            ProductUpdateRequest patch = productFixtures.getNewUpdateRequest();
            
            when(productRepository.findById(PRODUCT_EXISTING_ID)).thenReturn(Optional.of(product));
            when(categoryService.findById(CATEGORY_NON_EXISTING_ID)).thenThrow(new EntityNotFoundException());
            
            // When + Then
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.partialUpdateById(patch, PRODUCT_EXISTING_ID));
            verify(productRepository, never()).save(any());
        }
    }
}
