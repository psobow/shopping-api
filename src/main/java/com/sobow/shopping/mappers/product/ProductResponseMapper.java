package com.sobow.shopping.mappers.product;

import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductResponse;
import com.sobow.shopping.mappers.Mapper;
import java.util.List;

public interface ProductResponseMapper extends Mapper<Product, ProductResponse> {
    
    ProductResponse mapToDto(Product product, List<Long> imageIds);
}
