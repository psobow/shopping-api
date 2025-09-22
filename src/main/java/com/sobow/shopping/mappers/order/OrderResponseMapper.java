package com.sobow.shopping.mappers.order;

import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.order.OrderItem;
import com.sobow.shopping.domain.order.dto.OrderItemResponse;
import com.sobow.shopping.domain.order.dto.OrderResponse;
import com.sobow.shopping.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderResponseMapper implements Mapper<Order, OrderResponse> {
    
    private final Mapper<OrderItem, OrderItemResponse> orderItemResponseMapper;
    
    @Override
    public Order mapToEntity(OrderResponse orderResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public OrderResponse mapToDto(Order order) {
        return OrderResponse.builder()
                            .id(order.getId())
                            .status(order.getStatus().toString())
                            .createdAt(order.getCreatedAt())
                            .totalOrderPrice(order.getTotalPrice())
                            .itemResponseList(order.getOrderItems().stream()
                                                   .map(orderItemResponseMapper::mapToDto)
                                                   .toList())
                            .build();
    }
}
