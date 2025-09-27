package com.sobow.shopping.mappers.order.Impl;

import com.sobow.shopping.controllers.order.dto.OrderItemResponse;
import com.sobow.shopping.domain.order.OrderItem;
import com.sobow.shopping.mappers.order.OrderItemResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderItemResponseMapperImpl implements OrderItemResponseMapper {
    
    
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
