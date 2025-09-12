package com.sobow.shopping.services;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductRequest;
import java.util.List;

public interface ProductService {
    
    Product save(ProductRequest productRequest);
    
    Product findById(Long id);
    
    Product findProductWithCategoryAndImagesById(Long id);
    
    List<Product> findAllProductsWithCategoryAndImages();
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    Product partialUpdateById(ProductRequest patch, Long id);
    
    List<Product> findAll();
    
    List<Product> search(String name,
                         String brandName,
                         String categoryName);
}
