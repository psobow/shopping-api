package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductCreateRequest;
import com.sobow.shopping.domain.product.dto.ProductUpdateRequest;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryService categoryService;
    
    @Mock
    private Mapper<Product, ProductCreateRequest> productRequestMapper;
    
    @InjectMocks
    private ProductServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("create")
    class create {
        
        @Test
        public void create_should_ReturnSavedProduct_when_ValidInput() {
            // Given
            ProductCreateRequest request = fixtures.productCreateRequest();
            Product mapped = fixtures.productEntity();
            Category category = fixtures.categoryEntity();
            
            when(productRepository.existsByNameAndBrandName(any(), any())).thenReturn(false);
            when(productRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.findById(request.categoryId())).thenReturn(category);
            
            // When
            Product result = underTest.create(request);
            
            // Then
            assertSame(mapped, result);
            assertSame(category, result.getCategory());
        }
        
        @Test
        public void create_should_ThrowNotFound_when_CategoryIdDoesNotExists() {
            // Given
            ProductCreateRequest request = fixtures.withCategoryId(fixtures.nonExistingId())
                                                   .productCreateRequest();
            Product mapped = fixtures.productEntity();
            
            when(productRepository.existsByNameAndBrandName(any(), any())).thenReturn(false);
            when(productRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.create(request));
        }
        
        @Test
        public void create_should_ThrowAlreadyExists_when_ProductAlreadyExists() {
            // Given
            ProductCreateRequest request = fixtures.productCreateRequest();
            when(productRepository.existsByNameAndBrandName(any(), any())).thenReturn(true);
            
            // When & Then
            assertThrows(ProductAlreadyExistsException.class, () -> underTest.create(request));
        }
    }
    
    @Nested
    @DisplayName("partialUpdateById")
    class partialUpdateById {
        
        @Test
        public void partialUpdateById_should_ReturnUpdatedProduct_when_ValidInput() {
            // Given
            Category category = fixtures.categoryEntity();
            Product product = fixtures.productEntity();
            
            ReflectionTestUtils.setField(product, "id", fixtures.productId());
            category.addProductAndLink(product);
            
            ProductUpdateRequest patch = fixtures.withProductName("new product name")
                                                 .withCategoryId(null)
                                                 .productUpdateRequest();
            
            when(productRepository.findById(fixtures.productId())).thenReturn(Optional.of(product));
            when(productRepository.existsByNameAndBrandNameAndIdNot(
                "new product name", product.getBrandName(), fixtures.productId())).thenReturn(false);
            
            // When
            Product result = underTest.partialUpdateById(fixtures.productId(), patch);
            
            // Then
            assertSame(product, result);
            
            assertThat(product.getName()).isEqualTo("new product name");
        }
        
        @Test
        public void partialUpdateById_should_ThrowAlreadyExists_when_ProductAlreadyExists() {
            // Given
            Product product = fixtures.productEntity();
            ReflectionTestUtils.setField(product, "id", fixtures.productId());
            ProductUpdateRequest patch = fixtures.productUpdateRequest();
            
            when(productRepository.findById(fixtures.productId())).thenReturn(Optional.of(product));
            when(productRepository.existsByNameAndBrandNameAndIdNot(patch.name(), patch.brandName(), fixtures.productId())).thenReturn(true);
            
            // When & Then
            assertThrows(ProductAlreadyExistsException.class,
                         () -> underTest.partialUpdateById(fixtures.productId(), patch));
        }
        
        @Test
        public void partialUpdateById_should_ThrowNotFound_when_CategoryIdDoesNotExist() {
            // Given
            Product product = fixtures.productEntity();
            ProductUpdateRequest patch = fixtures.withCategoryId(fixtures.nonExistingId())
                                                 .productUpdateRequest();
            
            when(productRepository.findById(fixtures.productId())).thenReturn(Optional.of(product));
            when(categoryService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.partialUpdateById(fixtures.productId(), patch));
        }
    }
}
