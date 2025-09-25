package com.sobow.shopping.mappers.product.Impl;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductResponse;
import com.sobow.shopping.mappers.product.ProductResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapperImpl implements ProductResponseMapper {
    
    @Override
    public Product mapToEntity(ProductResponse productResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
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
                              .imagesId(product.getImages()
                                               .stream()
                                               .map(Image::getId)
                                               .toList())
                              .build();
    }
}
