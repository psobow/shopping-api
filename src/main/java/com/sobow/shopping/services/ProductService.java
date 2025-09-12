package com.sobow.shopping.services;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import java.util.List;

public interface ProductService {
    
    Product save(ProductCreateRequest productCreateRequest);
    
    Product findById(Long id);
    
    Product findProductWithCategoryAndImagesById(Long id);
    
    List<Product> findAllProductsWithCategoryAndImages();
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    Product partialUpdateById(ProductUpdateRequest patch, Long id);
    
    List<Product> findAll();
    
    List<Product> search(String name,
                         String brandName,
                         String categoryName);
}
