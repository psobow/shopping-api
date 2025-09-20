package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.order.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByUserProfile_User_IdAndId(Long userId, Long id);
    
    List<Order> findAllByUserProfile_User_IdOrderByCreatedAtDesc(Long userId);
}
