package com.sobow.shopping.mappers.product;

import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductCreateRequest;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ProductCreateRequestMapper implements Mapper<Product, ProductCreateRequest> {
    
    public Product mapToEntity(ProductCreateRequest productCreateRequest) {
        return Product.builder()
                      .name(productCreateRequest.name())
                      .brandName(productCreateRequest.brandName())
                      .price(productCreateRequest.price())
                      .availableQty(productCreateRequest.availableQuantity())
                      .description(productCreateRequest.description())
                      .build();
    }
    
    @Override
    public ProductCreateRequest mapToDto(Product product) {
        throw new UnsupportedOperationException("mapToDto is not implemented yet");
    }
}
