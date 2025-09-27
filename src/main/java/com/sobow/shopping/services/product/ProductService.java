package com.sobow.shopping.services.product;

import com.sobow.shopping.controllers.product.dto.ProductCreateRequest;
import com.sobow.shopping.controllers.product.dto.ProductResponse;
import com.sobow.shopping.controllers.product.dto.ProductUpdateRequest;
import com.sobow.shopping.domain.product.Product;
import java.util.List;

public interface ProductService {
    
    List<ProductResponse> mapProductsToResponsesWithImageIds(List<Product> products);
    
    Product findById(long id);
    
    List<Product> findAll();
    
    Product create(ProductCreateRequest createRequest);
    
    void deleteById(long id);
    
    Product partialUpdateById(long id, ProductUpdateRequest updateRequest);
    
    List<Product> lockForOrder(List<Long> ids);
    
    List<Product> search(String nameLike,
                         String brandName,
                         String categoryName);
}
