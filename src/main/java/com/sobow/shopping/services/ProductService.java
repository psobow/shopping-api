package com.sobow.shopping.services;

import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductCreateRequest;
import com.sobow.shopping.domain.product.dto.ProductUpdateRequest;
import java.util.List;

public interface ProductService {
    
    Product create(ProductCreateRequest createRequest);
    
    Product findById(long id);
    
    Product findWithCategoryAndImagesById(long id);
    
    List<Product> findAllWithCategoryAndImages();
    
    void deleteById(long id);
    
    boolean existsById(long id);
    
    Product partialUpdateById(long id, ProductUpdateRequest updateRequest);
    
    List<Product> findAll();
    
    List<Product> lockForOrder(List<Long> ids);
    
    List<Product> search(String nameLike,
                         String brandName,
                         String categoryName);
}
