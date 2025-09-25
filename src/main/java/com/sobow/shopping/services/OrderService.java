package com.sobow.shopping.services;

import com.sobow.shopping.domain.order.Order;
import java.util.List;

public interface OrderService {
    
    Order selfCreateOrder();
    
    Order selfFindById(long orderId);
    
    List<Order> selfFindAll();
    
    Order findByUserIdAndId(long userId, long orderId);
    
    List<Order> findAllByUserId(long userId);
}
