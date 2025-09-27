package com.sobow.shopping.controllers.admin;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.order.dto.OrderResponse;
import com.sobow.shopping.mappers.order.OrderResponseMapper;
import com.sobow.shopping.services.OrderService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin/users/{userId}/orders")
public class OrderManagementController {
    
    private final OrderService orderService;
    private final OrderResponseMapper orderResponseMapper;
    
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrder(
        @PathVariable @Positive long userId,
        @PathVariable @Positive long orderId
    ) {
        Order order = orderService.findByUserIdAndIdWithItems(userId, orderId);
        OrderResponse response = orderResponseMapper.mapToDto(order);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllOrdersByUserId(
        @PathVariable @Positive long userId
    ) {
        List<Order> orders = orderService.findAllByUserIdWithItems(userId);
        List<OrderResponse> response = orders.stream().map(orderResponseMapper::mapToDto).toList();
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
}
