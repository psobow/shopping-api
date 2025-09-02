package com.sobow.shopping.mappers;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.dto.ProductRequest;
import com.sobow.shopping.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    
    private final CategoryRepository categoryRepository;
    
    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public Product mapToEntity(ProductRequest productRequest) {
        Category category =
            categoryRepository.findById(productRequest.categoryId()).orElseThrow(() -> new EntityNotFoundException(
                "Category with id " + productRequest.categoryId() + " not found"));
        
        return new Product().builder()
                            .name(productRequest.name())
                            .brandName(productRequest.brandName())
                            .price(productRequest.price())
                            .availableQuantity(productRequest.availableQuantity())
                            .description(productRequest.description())
                            .category(category)
                            .build();
    }
    
}
