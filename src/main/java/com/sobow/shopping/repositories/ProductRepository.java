package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.product.Product;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Optional<Product> findByIdWithCategoryAndImages(long id);
    
    @Query("""
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.category
        LEFT JOIN FETCH p.images
        """)
    List<Product> findAllWithCategoryAndImages();
    
    boolean existsByNameAndBrandName(String name, String brandName);
    
    boolean existsByNameAndBrandNameAndIdNot(String name, String brandName, long id);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p
        FROM Product p
        WHERE p.id IN :ids
        """)
    List<Product> findAllForUpdate(@Param("ids") List<Long> ids);
}
