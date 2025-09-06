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
import com.sobow.shopping.mappers.ProductMapper;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.Impl.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
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
    private ProductMapper productMapper;
    
    @InjectMocks
    private ProductServiceImpl underTest;
    
    @Test
    public void save_Success() {
        // Given
        Long categoryId = 1L;
        ProductCreateRequest dto = new ProductCreateRequest(
            "ProductName", "Brand", new BigDecimal("10.00"), 5, "Desc", categoryId);
        
        Product mapped = new Product();       // what mapper returns
        Category category = new Category();   // what repo returns
        
        when(productMapper.mapToEntity(dto)).thenReturn(mapped);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(mapped)).thenReturn(mapped);
        // When
        Product result = underTest.save(dto);
        
        // Then
        assertSame(mapped, result);                // we got back what repository returned
        assertSame(category, mapped.getCategory()); // category was set on the entity
    }
    
    @Test
    public void save_missingCategory_Throws() {
        // Given
        long missingId = 999L;
        ProductCreateRequest dto = new ProductCreateRequest(
            "ProductName", "Brand", new BigDecimal("10.00"), 5, "Desc", missingId);
        when(categoryRepository.findById(missingId)).thenReturn(Optional.empty());
        
        // When + Then
        assertThrows(EntityNotFoundException.class, () -> underTest.save(dto));
        verify(productRepository, never()).save(any());
    }
    
    @Test
    public void partialUpdateById_Success() {
        // Given
        Long productId = 2L;
        Product product = new Product();
        
        Long newCategoryId = 3L;
        Category newCategory = new Category();
        newCategory.setName("newCategoryName");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(product)).thenReturn(product);
        
        // When
        ProductUpdateRequest patch =
            new ProductUpdateRequest("newProductName", null, null, null, null, newCategoryId);
        Product result = underTest.partialUpdateById(patch, productId);
        
        // Then
        assertSame(product, result);
        assertEquals("newProductName", result.getName());
        assertEquals("newCategoryName", result.getCategory().getName());
        
        // Interactions with mock
        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
        verifyNoMoreInteractions(productRepository);
    }
    
    @Test
    public void partialUpdateById_NonExistingId_Throws() {
        // Given
        Long productId = 2L;
        Product product = new Product();
        
        Long nonExistingCategoryId = 9999L;
        ProductUpdateRequest patch =
            new ProductUpdateRequest(null, null, null, null, null, nonExistingCategoryId);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(nonExistingCategoryId)).thenReturn(Optional.empty());
        
        // When + Then
        assertThrows(EntityNotFoundException.class,
                     () -> underTest.partialUpdateById(patch, productId));
        verify(productRepository, never()).save(any());
    }
}
