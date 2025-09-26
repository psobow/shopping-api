package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.category.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    @Query("""
        SELECT DISTINCT c
        FROM Category c
        LEFT JOIN FETCH c.products
        WHERE c.id = :id
        """)
    Optional<Category> findByIdWithProducts(long id);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, long id);
}
