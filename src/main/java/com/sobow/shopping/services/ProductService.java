package com.sobow.shopping.services;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import java.util.List;

public interface ProductService {
    
    Product create(ProductCreateRequest productCreateRequest);
    
    Product findById(long id);
    
    Product findWithCategoryAndImagesById(long id);
    
    List<Product> findAllWithCategoryAndImages();
    
    void deleteById(long id);
    
    boolean existsById(long id);
    
    Product partialUpdateById(ProductUpdateRequest patch, long id);
    
    List<Product> findAll();
    
    List<Product> search(String nameLike,
                         String brandName,
                         String categoryName);
}
