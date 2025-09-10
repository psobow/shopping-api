package com.sobow.shopping.services;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductRequest;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    Product save(ProductRequest productRequest);
    
    Product findById(Long id);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    Product partialUpdateById(ProductRequest patch, Long id);
    
    List<Product> findAll();
    
    List<Product> search(Optional<String> name,
                         Optional<String> brand,
                         Optional<String> category);
}
