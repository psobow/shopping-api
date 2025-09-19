package com.sobow.shopping.services;

import com.sobow.shopping.domain.order.Order;

public interface OrderService {
    
    Order createOrder(long userId);
    
    Order findById(long orderId);
}
