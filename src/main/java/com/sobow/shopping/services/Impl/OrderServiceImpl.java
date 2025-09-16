package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.entities.Order;
import com.sobow.shopping.repositories.OrderRepository;
import com.sobow.shopping.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    
    @Override
    public Order createOrder(long userId) {
        throw new UnsupportedOperationException("createOrder is not implemented yet");
    }
    
    @Override
    public Order findById(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
            () -> new EntityNotFoundException("Order with id " + orderId + " not found"));
    }
}
