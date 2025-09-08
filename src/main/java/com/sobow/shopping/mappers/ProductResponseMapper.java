package com.sobow.shopping.mappers;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.responses.ProductResponse;

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
                                   product.getCategory().getId());
    }
}
