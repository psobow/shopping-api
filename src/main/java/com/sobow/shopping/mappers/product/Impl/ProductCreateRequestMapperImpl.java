package com.sobow.shopping.mappers.product.Impl;

import com.sobow.shopping.controllers.product.dto.ProductCreateRequest;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.mappers.product.ProductCreateRequestMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductCreateRequestMapperImpl implements ProductCreateRequestMapper {
    
    public Product mapToEntity(ProductCreateRequest productCreateRequest) {
        return Product.builder()
                      .name(productCreateRequest.name())
                      .brandName(productCreateRequest.brandName())
                      .price(productCreateRequest.price())
                      .availableQty(productCreateRequest.availableQuantity())
                      .description(productCreateRequest.description())
                      .build();
    }
    
}
