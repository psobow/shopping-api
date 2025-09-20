package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.order.OrderItem;
import com.sobow.shopping.domain.order.OrderItemResponse;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class OrderItemResponseMapper implements Mapper<OrderItem, OrderItemResponse> {
    
    @Override
    public OrderItem mapToEntity(OrderItemResponse orderItemResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public OrderItemResponse mapToDto(OrderItem orderItem) {
        return OrderItemResponse.builder()
                                .id(orderItem.getId())
                                .orderId(orderItem.getOrder().getId())
                                .requestedQty(orderItem.getRequestedQty())
                                .productName(orderItem.getProductName())
                                .productBrandName(orderItem.getProductBrandName())
                                .productPrice(orderItem.getProductPrice())
                                .totalItemPrice(orderItem.getTotalPrice())
                                .build();
    }
}
