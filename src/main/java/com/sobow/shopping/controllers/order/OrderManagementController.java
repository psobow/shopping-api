package com.sobow.shopping.controllers.order;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.order.dto.OrderResponse;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.mappers.order.OrderResponseMapper;
import com.sobow.shopping.services.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
    name = "Order Management Controller",
    description = "API to manage orders by Admin"
)
public class OrderManagementController {
    
    private final OrderService orderService;
    private final OrderResponseMapper orderResponseMapper;
    
    @Operation(
        summary = "Get user's order",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameters({
        @Parameter(name = "userId", required = true, description = "User ID"),
        @Parameter(name = "orderId", required = true, description = "Order ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDto> getOrder(
        @PathVariable @Positive long userId,
        @PathVariable @Positive long orderId
    ) {
        Order order = orderService.findByUserIdAndIdWithItems(userId, orderId);
        OrderResponse response = orderResponseMapper.mapToDto(order);
        return ResponseEntity.ok(new ApiResponseDto("Found", response));
    }
    
    @Operation(
        summary = "List user's orders",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameters({
        @Parameter(name = "userId", required = true, description = "User ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto> getAllOrdersByUserId(
        @PathVariable @Positive long userId
    ) {
        List<Order> orders = orderService.findAllByUserIdWithItems(userId);
        List<OrderResponse> response = orders.stream().map(orderResponseMapper::mapToDto).toList();
        return ResponseEntity.ok(new ApiResponseDto("Found", response));
    }
}
