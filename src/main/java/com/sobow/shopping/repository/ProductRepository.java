package com.sobow.shopping.repository;

import com.sobow.shopping.domain.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByName(String name);
    
    List<Product> findByCategory_Name(String categoryName);
    
    List<Product> findByCategory_NameAndBrandName(String categoryName, String brandName);
    
    List<Product> findByBrandName(String brandName);
    
    List<Product> findByBrandNameAndName(String brandName, String name);
    
    Long countByBrandNameAndName(String brandName, String name);
}
