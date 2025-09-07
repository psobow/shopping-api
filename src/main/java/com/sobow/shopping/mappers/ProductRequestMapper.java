package com.sobow.shopping.mappers;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductRequestMapper implements Mapper<Product, ProductCreateRequest> {
    
    public Product mapToEntity(ProductCreateRequest productCreateRequest) {
        return new Product().builder()
                            .name(productCreateRequest.name())
                            .brandName(productCreateRequest.brandName())
                            .price(productCreateRequest.price())
                            .availableQuantity(productCreateRequest.availableQuantity())
                            .description(productCreateRequest.description())
                            .build();
    }
    
    @Override
    public ProductCreateRequest mapToDto(Product product) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
