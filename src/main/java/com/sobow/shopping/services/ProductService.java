package com.sobow.shopping.services;

import com.sobow.shopping.domain.Product;
import java.util.List;

public interface ProductService {
    
    Product save(Product product);
    
    Product findById(Long id);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    Product partialUpdateById(Product product, Long id);
    
    List<Product> findAll();
    
    List<Product> findByName(String name);
    
    List<Product> findByCategory_Name(String categoryName);
    
    List<Product> findByCategory_NameAndBrandName(String categoryName, String brandName);
    
    List<Product> findByBrandName(String brandName);
    
    List<Product> findByBrandNameAndName(String brandName, String name);
    
    Long countByBrandNameAndName(String brandName, String name);
}
