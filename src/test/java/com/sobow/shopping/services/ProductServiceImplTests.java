package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.Impl.ProductServiceImpl;
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
    
    @InjectMocks
    private ProductServiceImpl underTest;
    
    @Test
    public void partialUpdateById_updatesOnlyNonNullFields() {
        // Given
        Product existing = new Product();
        Long id = 1L;
        existing.setId(id);
        existing.setName("productName");
        existing.setBrandName("brandName");
        existing.setPrice(new BigDecimal(10.0));
        existing.setAvailableQuantity(1);
        existing.setDescription("Description");
        Category category = new Category();
        category.setName("categoryName");
        existing.setCategory(category);
        
        Product patch = new Product();
        patch.setName("newProductName");
        Category newCategory = new Category();
        newCategory.setName("newCategoryName");
        patch.setCategory(newCategory);
        
        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);
        
        // When
        Product result = underTest.partialUpdateById(patch, id);
        
        // Then
        // Changed fields
        assertEquals("newProductName", result.getName());
        assertEquals("newCategoryName", result.getCategory().getName());
        
        // Other fields stay the same
        assertSame(existing, result);
        assertEquals(1L, result.getId());
        assertEquals("brandName", result.getBrandName());
        assertEquals(new BigDecimal(10.0), result.getPrice());
        assertEquals(1, result.getAvailableQuantity());
        
        // Interactions with mock
        verify(productRepository).findById(id);
        verify(productRepository).save(existing);
        verifyNoMoreInteractions(productRepository);
    }
}
