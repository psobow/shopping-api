package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.cart.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    boolean existsByCartIdAndProductId(long cartId, long productId);
    
    Optional<CartItem> findByCartIdAndId(long cartId, long id);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteAllByProduct_Id(long productId);
    
}
