package com.sobow.shopping.services;

import com.sobow.shopping.domain.order.Order;
import java.util.List;

public interface OrderService {
    
    Order selfCreateOrder();
    
    Order selfFindByIdWithItems(long orderId);
    
    List<Order> selfFindAllWithItems();
    
    Order findByUserIdAndIdWithItems(long userId, long orderId);
    
    List<Order> findAllByUserIdWithItems(long userId);
}
