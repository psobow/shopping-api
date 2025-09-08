package com.sobow.shopping.mappers;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductRequestMapper implements Mapper<Product, ProductRequest> {
    
    public Product mapToEntity(ProductRequest productRequest) {
        return new Product().builder()
                            .name(productRequest.name())
                            .brandName(productRequest.brandName())
                            .price(productRequest.price())
                            .availableQuantity(productRequest.availableQuantity())
                            .description(productRequest.description())
                            .build();
    }
    
    @Override
    public ProductRequest mapToDto(Product product) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
