package com.sobow.shopping.mappers.product.Impl;

import com.sobow.shopping.controllers.product.dto.ProductResponse;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.mappers.product.ProductResponseMapper;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapperImpl implements ProductResponseMapper {
    
    @Override
    public ProductResponse mapToDto(Product product) {
        return ProductResponse.builder()
                              .id(product.getId())
                              .name(product.getName())
                              .brandName(product.getBrandName())
                              .price(product.getPrice())
                              .availableQty(product.getAvailableQty())
                              .description(product.getDescription())
                              .categoryId(product.getCategory().getId())
                              .build();
    }
    
    @Override
    public ProductResponse mapToDto(Product product, List<Long> imageIds) {
        return ProductResponse.builder()
                              .id(product.getId())
                              .name(product.getName())
                              .brandName(product.getBrandName())
                              .price(product.getPrice())
                              .availableQty(product.getAvailableQty())
                              .description(product.getDescription())
                              .categoryId(product.getCategory().getId())
                              .imageIds(imageIds)
                              .build();
    }
}
