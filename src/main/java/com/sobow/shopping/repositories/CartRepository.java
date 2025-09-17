package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.entities.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    boolean existsByUserProfile_UserId(long userId);
    
    @Query("""
        SELECT DISTINCT c
        FROM Cart c
        LEFT JOIN FETCH c.cartItems
        WHERE c.id = :id
        """)
    Optional<Cart> findByIdWithItems(long id);
}
