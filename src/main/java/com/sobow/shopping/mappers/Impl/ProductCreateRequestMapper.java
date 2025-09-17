package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.entities.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.mappers.Mapper;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class ProductCreateRequestMapper implements Mapper<Product, ProductCreateRequest> {
    
    public Product mapToEntity(ProductCreateRequest productCreateRequest) {
        return new Product().builder()
                            .name(productCreateRequest.name())
                            .brandName(productCreateRequest.brandName())
                            .price(productCreateRequest.price())
                            .availableQty(productCreateRequest.availableQuantity())
                            .description(productCreateRequest.description())
                            .images(new ArrayList<>())
                            .build();
    }
    
    @Override
    public ProductCreateRequest mapToDto(Product product) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
