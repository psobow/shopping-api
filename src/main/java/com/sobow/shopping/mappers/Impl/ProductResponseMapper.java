package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.entities.Image;
import com.sobow.shopping.domain.entities.Product;
import com.sobow.shopping.domain.responses.ProductResponse;
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
        return new ProductResponse(product.getId(),
                                   product.getName(),
                                   product.getBrandName(),
                                   product.getPrice(),
                                   product.getAvailableQuantity(),
                                   product.getDescription(),
                                   product.getCategory().getId(),
                                   product.getImages()
                                          .stream()
                                          .map(Image::getId)
                                          .toList()
        );
    }
}
