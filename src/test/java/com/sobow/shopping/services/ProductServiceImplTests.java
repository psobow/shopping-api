package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import com.sobow.shopping.exceptions.ProductAlreadyExistsException;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.Impl.ProductServiceImpl;
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
public class ProductServiceImplTests {
    
    @Mock
    private ProductRepository productRepository;
    
    private final static long PRODUCT_EXISTING_ID = 1L;
    
    private TestFixtures fixtures = new TestFixtures();
    
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
            ProductCreateRequest request = fixtures.productCreateRequest();
            Product mapped = fixtures.productEntity();
            Category category = mapped.getCategory();
            
            when(productRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.findById(request.categoryId())).thenReturn(category);
            when(productRepository.save(mapped)).thenReturn(mapped);
            // When
            Product result = underTest.save(request);
            
            // Then
            assertSame(mapped, result);
            assertSame(category, mapped.getCategory());
        }
        
        @Test
        public void save_should_ThrowNotFound_when_CategoryIdDoesNotExists() {
            // Given
            fixtures.withCategoryId(CATEGORY_NON_EXISTING_ID);
            ProductCreateRequest request = fixtures.productCreateRequest();
            when(categoryService.findById(CATEGORY_NON_EXISTING_ID)).thenThrow(new EntityNotFoundException());
            
            // When + Then
            assertThrows(EntityNotFoundException.class, () -> underTest.save(request));
            verify(productRepository, never()).save(any());
        }
        
        @Test
        public void save_should_ThrowAlreadyExists_when_Duplicate() {
            ProductCreateRequest request = fixtures.productCreateRequest();
            when(productRepository.existsByNameAndBrandName(request.name(), request.brandName())).thenReturn(true);
            // When + Then
            assertThrows(ProductAlreadyExistsException.class, () -> underTest.save(request));
            verify(productRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("partialUpdateById")
    class partialUpdateById {
        
        @Test
        public void partialUpdateById_should_ReturnUpdatedProduct_when_ValidInput() {
            // Given
            fixtures.withProductId(PRODUCT_EXISTING_ID)
                    .withCategoryId(CATEGORY_EXISTING_ID);
            
            Product product = fixtures.productEntity();
            String oldName = "old name - to be updated";
            product.setName(oldName);
            Category category = fixtures.categoryEntity();
            ProductUpdateRequest patch = fixtures.productUpdateRequest();
            
            when(productRepository.findById(PRODUCT_EXISTING_ID)).thenReturn(Optional.of(product));
            when(categoryService.findById(CATEGORY_EXISTING_ID)).thenReturn(category);
            when(productRepository.save(product)).thenReturn(product);
            
            // When
            Product result = underTest.partialUpdateById(patch, PRODUCT_EXISTING_ID);
            
            // Then
            assertSame(product, result);
            
            assertEquals(patch.name(), result.getName());
            assertNotEquals(oldName, product.getName());
            assertEquals(category.getName(), result.getCategory().getName());
        }
        
        @Test
        public void partialUpdateById_should_ThrowNotFound_when_CategoryIdDoesNotExist() {
            // Given
            fixtures.withProductId(PRODUCT_EXISTING_ID)
                    .withCategoryId(CATEGORY_NON_EXISTING_ID);
            
            Product product = fixtures.productEntity();
            ProductUpdateRequest patch = fixtures.productUpdateRequest();
            
            when(productRepository.findById(PRODUCT_EXISTING_ID)).thenReturn(Optional.of(product));
            when(categoryService.findById(CATEGORY_NON_EXISTING_ID)).thenThrow(new EntityNotFoundException());
            
            // When + Then
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.partialUpdateById(patch, PRODUCT_EXISTING_ID));
            verify(productRepository, never()).save(any());
        }
        
        @Test
        public void partialUpdateById_should_ThrowAlreadyExists_when_Duplicate() {
            fixtures.withProductId(PRODUCT_EXISTING_ID);
            
            Product product = fixtures.productEntity();
            ProductUpdateRequest request = fixtures.productUpdateRequest();
            
            when(productRepository.findById(PRODUCT_EXISTING_ID)).thenReturn(Optional.of(product));
            when(productRepository.existsByNameAndBrandName(request.name(), request.brandName())).thenReturn(true);
            // When + Then
            assertThrows(ProductAlreadyExistsException.class, () -> underTest.partialUpdateById(request, PRODUCT_EXISTING_ID));
            verify(productRepository, never()).save(any());
        }
    }
}
