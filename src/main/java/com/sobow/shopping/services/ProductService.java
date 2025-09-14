package com.sobow.shopping.services;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import java.util.List;

public interface ProductService {
    
    Product save(ProductCreateRequest productCreateRequest);
    
    Product findById(long id);
    
    Product findProductWithCategoryAndImagesById(long id);
    
    List<Product> findAllProductsWithCategoryAndImages();
    
    void deleteById(long id);
    
    boolean existsById(long id);
    
    Product partialUpdateById(ProductUpdateRequest patch, long id);
    
    List<Product> findAll();
    
    List<Product> search(String name,
                         String brandName,
                         String categoryName);
}
