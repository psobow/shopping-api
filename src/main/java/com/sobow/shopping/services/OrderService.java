package com.sobow.shopping.services;

import com.sobow.shopping.domain.order.Order;
import java.util.List;

public interface OrderService {
    
    Order createOrder(long userId);
    
    Order findByUserIdAndId(long userId, long orderId);
    
    List<Order> findAllByUserId(long userId);
}
