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
    
    @Mock
    private CategoryService categoryService;
    
    @Mock
    private Mapper<Product, ProductCreateRequest> productRequestMapper;
    
    @InjectMocks
    private ProductServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("save")
    class save {
        
        @Test
        public void save_should_ReturnSavedProduct_when_ValidInput() {
            // Given
            ProductCreateRequest request = fixtures.productCreateRequest();
            
            Product mapped = fixtures.withProductId(null)
                                     .productEntity();
            
            Category category = mapped.getCategory();
            
            when(productRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.findById(request.categoryId())).thenReturn(category);
            when(productRepository.save(mapped)).thenAnswer(inv -> {
                Product p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });
            // When
            Product result = underTest.save(request);
            
            // Then
            assertSame(mapped, result);
            assertEquals(1L, result.getId());
        }
        
        @Test
        public void save_should_ThrowNotFound_when_CategoryIdDoesNotExists() {
            // Given
            ProductCreateRequest request = fixtures.withCategoryId(fixtures.nonExistingId()).productCreateRequest();
            when(categoryService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When + Then
            assertThrows(EntityNotFoundException.class, () -> underTest.save(request));
            verify(productRepository, never()).save(any());
        }
        
        @Test
        public void save_should_ThrowAlreadyExists_when_ProductAlreadyExists() {
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
            Product product = fixtures.productEntity();
            String oldProductName = product.getName();
            
            ProductUpdateRequest patch = fixtures.withCategoryId(null)
                                                 .withProductName("new product name")
                                                 .productUpdateRequest();
            
            when(productRepository.findById(fixtures.productId())).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);
            
            // When
            Product result = underTest.partialUpdateById(patch, fixtures.productId());
            
            // Then
            assertSame(product, result);
            assertEquals(patch.name(), result.getName());
            assertNotEquals(oldProductName, product.getName());
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
            verify(productRepository, never()).save(any());
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
            verify(productRepository, never()).save(any());
        }
    }
}
