package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.order.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.orderItems
        WHERE o.userProfile.user.id = :userId AND o.id = :id
        """)
    Optional<Order> findByUserIdAndIdWithOrderItems(Long userId, Long id);
    
    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.orderItems
        WHERE o.userProfile.user.id = :userId
        """)
    List<Order> findAllByUserIdWithOrderItems(Long userId);
}
