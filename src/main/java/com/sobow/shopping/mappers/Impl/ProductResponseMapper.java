package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.ProductResponse;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapper implements Mapper<Product, ProductResponse> {
    
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
