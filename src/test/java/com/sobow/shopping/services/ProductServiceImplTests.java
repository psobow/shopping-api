package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductCreateRequest;
import com.sobow.shopping.domain.product.dto.ProductUpdateRequest;
import com.sobow.shopping.exceptions.ProductAlreadyExistsException;
import com.sobow.shopping.mappers.product.ProductCreateRequestMapper;
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
    private ProductCreateRequestMapper productCreateRequestMapper;
    
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
            when(productCreateRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.findById(request.categoryId())).thenReturn(category);
            
            // When
            Product result = underTest.create(request);
            
            // Then
            // Assert: uniqueness check performed with request name & brand
            verify(productRepository).existsByNameAndBrandName(request.name(), request.brandName());
            
            // Assert: mapper used to create a new Product from request
            verify(productCreateRequestMapper).mapToEntity(request);
            
            // Assert: category was loaded
            verify(categoryService).findById(request.categoryId());
            
            // Assert: service returns the same managed instance created by the mapper
            assertThat(result).isSameAs(mapped);
            
            // Assert: product linked to the loaded category
            assertThat(result.getCategory()).isSameAs(category);
            assertThat(category.getProducts()).containsExactly(result);
        }
        
        @Test
        public void create_should_ThrowNotFound_when_CategoryIdDoesNotExists() {
            // Given
            ProductCreateRequest request = fixtures.withCategoryId(fixtures.nonExistingId())
                                                   .productCreateRequest();
            Product mapped = fixtures.productEntity();
            
            when(productRepository.existsByNameAndBrandName(any(), any())).thenReturn(false);
            when(productCreateRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            // Assert: Throw when category not found
            assertThrows(EntityNotFoundException.class, () -> underTest.create(request));
            
            // Assert: uniqueness check performed with request name & brand
            verify(productRepository).existsByNameAndBrandName(request.name(), request.brandName());
            
            // Assert: mapper was invoked
            verify(productCreateRequestMapper).mapToEntity(request);
            
            // Assert: category lookup attempted with the provided (non-existing) id
            verify(categoryService).findById(request.categoryId());
            
            // Assert: no persistence/linking after failure
            assertThat(mapped.getCategory()).isNull();
        }
        
        @Test
        public void create_should_ThrowAlreadyExists_when_ProductAlreadyExists() {
            // Given
            ProductCreateRequest request = fixtures.productCreateRequest();
            when(productRepository.existsByNameAndBrandName(any(), any())).thenReturn(true);
            
            // When & Then
            // Assert: throws when product with same name & brand already exists
            assertThrows(ProductAlreadyExistsException.class, () -> underTest.create(request));
            
            // Assert: uniqueness check was performed with exact args
            verify(productRepository).existsByNameAndBrandName(request.name(), request.brandName());
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
            String newProductName = "new product name";
            ProductUpdateRequest patch = fixtures.withProductName(newProductName)
                                                 .withCategoryId(null)
                                                 .productUpdateRequest();
            
            when(productRepository.findById(fixtures.productId())).thenReturn(Optional.of(product));
            when(productRepository.existsByNameAndBrandNameAndIdNot(
                newProductName, product.getBrandName(), fixtures.productId())).thenReturn(false);
            
            // When
            Product result = underTest.partialUpdateById(fixtures.productId(), patch);
            
            // Then
            // Assert: repository looked up the entity
            verify(productRepository).findById(fixtures.productId());
            
            // Assert: uniqueness check performed
            verify(productRepository)
                .existsByNameAndBrandNameAndIdNot(newProductName, product.getBrandName(), fixtures.productId());
            
            // Assert: service returns the same instance
            assertThat(result).isSameAs(product);
            
            // Assert: the patched field changed
            assertThat(product.getName()).isEqualTo(newProductName);
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
            // Assert: throw when product already exists
            assertThrows(ProductAlreadyExistsException.class,
                         () -> underTest.partialUpdateById(fixtures.productId(), patch));
            
            // Assert: repository looked up the entity
            verify(productRepository).findById(fixtures.productId());
            
            // Assert: uniqueness check performed with patch name, brand and excluded id
            verify(productRepository)
                .existsByNameAndBrandNameAndIdNot(patch.name(), patch.brandName(), fixtures.productId());
            
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
            // Assert: throws when category lookup fails
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.partialUpdateById(fixtures.productId(), patch));
            
            // Assert: repository looked up the product
            verify(productRepository).findById(fixtures.productId());
            
            // Assert: category lookup was attempted with the provided id
            verify(categoryService).findById(fixtures.nonExistingId());
            
        }
    }
}
