package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.entities.Category;
import com.sobow.shopping.domain.entities.Product;
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
            Product mapped = fixtures.withProductId(null)
                                     .productEntity();
            Category category = fixtures.categoryEntity();
            
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
            when(categoryService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When + Then
            assertThrows(EntityNotFoundException.class, () -> underTest.create(request));
        }
        
        @Test
        public void create_should_ThrowAlreadyExists_when_ProductAlreadyExists() {
            ProductCreateRequest request = fixtures.productCreateRequest();
            when(productRepository.existsByNameAndBrandName(request.name(), request.brandName())).thenReturn(true);
            // When + Then
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
            category.addProductAndLink(product);
            
            Long differentCategoryId = 15L;
            Category differentCategory = fixtures.withCategoryId(differentCategoryId)
                                                 .categoryEntity();
            
            ProductUpdateRequest patch = fixtures.withCategoryId(differentCategoryId)
                                                 .withProductName("new product name")
                                                 .productUpdateRequest();
            
            when(productRepository.findById(fixtures.productId())).thenReturn(Optional.of(product));
            when(categoryService.findById(differentCategoryId)).thenReturn(differentCategory);
            
            // When
            Product result = underTest.partialUpdateById(patch, fixtures.productId());
            
            // Then
            assertSame(product, result);
            
            assertThat(product.getCategory().getId()).isEqualTo(differentCategoryId);
            assertThat(product.getName()).isEqualTo("new product name");
        }
        
        @Test
        public void partialUpdateById_should_ThrowNotFound_when_CategoryIdDoesNotExist() {
            // Given
            Product product = fixtures.productEntity();
            ProductUpdateRequest patch = fixtures.withCategoryId(fixtures.nonExistingId())
                                                 .productUpdateRequest();
            
            when(productRepository.findById(fixtures.productId())).thenReturn(Optional.of(product));
            when(categoryService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When + Then
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.partialUpdateById(patch, fixtures.productId()));
        }
        
        @Test
        public void partialUpdateById_should_ThrowAlreadyExists_when_ProductAlreadyExists() {
            Product product = fixtures.productEntity();
            ProductUpdateRequest patch = fixtures.productUpdateRequest();
            
            when(productRepository.findById(fixtures.productId())).thenReturn(Optional.of(product));
            when(productRepository.existsByNameAndBrandName(patch.name(), patch.brandName())).thenReturn(true);
            // When + Then
            assertThrows(ProductAlreadyExistsException.class,
                         () -> underTest.partialUpdateById(patch, fixtures.productId()));
        }
    }
}
