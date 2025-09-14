package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    @Query("""
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.category
        LEFT JOIN FETCH p.images
        WHERE p.id = :id
        """)
    Optional<Product> findProductWithCategoryAndImagesById(long id);
    
    @Query("""
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.category
        LEFT JOIN FETCH p.images
        """)
    List<Product> findAllProductsWithCategoryAndImages();
    
    boolean existsByNameAndBrandName(String name, String brandName);
}
