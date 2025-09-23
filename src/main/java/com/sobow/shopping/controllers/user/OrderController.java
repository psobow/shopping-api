package com.sobow.shopping.controllers.user;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.order.dto.OrderResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.OrderService;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users/{userId}/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    private final Mapper<Order, OrderResponse> orderResponseMapper;
    
    @PostMapping
    public ResponseEntity<ApiResponse> createOrder(@PathVariable @Positive long userId) {
        Order order = orderService.createOrder(userId);
        OrderResponse response = orderResponseMapper.mapToDto(order);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()   // /api/users/{userId}/orders
            .path("/{id}")             // /api/users/{userId}/orders/{id}
            .buildAndExpand(order.getId())
            .toUri();
        
        return ResponseEntity.created(location)
                             .body(new ApiResponse("Created", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllOrdersByUserId(
        @PathVariable @Positive long userId
    ) {
        List<Order> orders = orderService.findAllByUserId(userId);
        List<OrderResponse> response = orders.stream().map(orderResponseMapper::mapToDto).toList();
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrder(
        @PathVariable @Positive long userId,
        @PathVariable @Positive long orderId
    ) {
        Order order = orderService.findByUserIdAndId(userId, orderId);
        OrderResponse response = orderResponseMapper.mapToDto(order);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
}
