package com.sobow.shopping.controllers.order;

import com.sobow.shopping.controllers.ApiResponse;
import com.sobow.shopping.controllers.order.dto.OrderResponse;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.mappers.order.OrderResponseMapper;
import com.sobow.shopping.services.order.OrderService;
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
@RequestMapping("${api.prefix}/users/me/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    private final OrderResponseMapper orderResponseMapper;
    
    @PostMapping
    public ResponseEntity<ApiResponse> selfCreateOrder() {
        Order order = orderService.selfCreateOrder();
        OrderResponse response = orderResponseMapper.mapToDto(order);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(order.getId())
            .toUri();
        
        return ResponseEntity.created(location)
                             .body(new ApiResponse("Created", response));
    }
    
    @GetMapping("{orderId}")
    public ResponseEntity<ApiResponse> selfGetOrderById(
        @PathVariable @Positive long orderId
    ) {
        Order order = orderService.selfFindByIdWithItems(orderId);
        OrderResponse response = orderResponseMapper.mapToDto(order);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> selfGetAllOrders() {
        List<Order> orders = orderService.selfFindAllWithItems();
        List<OrderResponse> response = orders.stream().map(orderResponseMapper::mapToDto).toList();
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
}
