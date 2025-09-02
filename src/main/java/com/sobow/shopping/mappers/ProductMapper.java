package com.sobow.shopping.mappers;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.dto.ProductCreateRequest;
import com.sobow.shopping.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements Mapper<Product, ProductCreateRequest> {
    
    private final CategoryRepository categoryRepository;
    
    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Product mapToEntity(ProductCreateRequest productCreateRequest) {
        Category category =
            categoryRepository.findById(productCreateRequest.categoryId())
                              .orElseThrow(() -> new EntityNotFoundException(
                                  "Category with id " + productCreateRequest.categoryId() + " not found"));
        
        return new Product().builder()
                            .name(productCreateRequest.name())
                            .brandName(productCreateRequest.brandName())
                            .price(productCreateRequest.price())
                            .availableQuantity(productCreateRequest.availableQuantity())
                            .description(productCreateRequest.description())
                            .category(category)
                            .build();
    }
}
