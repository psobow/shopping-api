package com.sobow.shopping.domain.product;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.hibernate.jpa.SpecHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    @Query("""
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.images
        WHERE p.id = :id
        """)
    Optional<Product> findByIdWithImages(long id);
    
    @Query("""
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.images
        """)
    List<Product> findAllWithImages();
    
    boolean existsByNameAndBrandName(String name, String brandName);
    
    boolean existsByNameAndBrandNameAndIdNot(String name, String brandName, long id);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p
        FROM Product p
        WHERE p.id IN :ids
        """)
    @QueryHints(@QueryHint(name = SpecHints.HINT_SPEC_LOCK_TIMEOUT, value = "5000"))
        // value in ms
    List<Product> findAllForUpdate(List<Long> ids);
}
