package com.sobow.shopping.services;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.dto.ProductRequest;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    Product save(ProductRequest productRequest);
    
    Optional<Product> findById(Long id);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    Product partialUpdateById(ProductRequest productRequest, Long id);
    
    List<Product> findAll();
    
    List<Product> findByName(String name);
    
    List<Product> findByCategory_Name(String categoryName);
    
    List<Product> findByCategory_NameAndBrandName(String categoryName, String brandName);
    
    List<Product> findByBrandName(String brandName);
    
    List<Product> findByBrandNameAndName(String brandName, String name);
    
    Long countByBrandNameAndName(String brandName, String name);
}
