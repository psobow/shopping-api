package com.sobow.shopping.services;

import com.sobow.shopping.domain.Order;

public interface OrderService {
    
    Order createOrder(long userId);
    
    Order findById(long orderId);
}
