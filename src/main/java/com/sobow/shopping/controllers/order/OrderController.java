package com.sobow.shopping.controllers.order;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.order.dto.OrderResponse;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.mappers.order.OrderResponseMapper;
import com.sobow.shopping.services.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
    name = "Order Controller",
    description = "API to manage order by authenticated user"
)
public class OrderController {
    
    private final OrderService orderService;
    
    private final OrderResponseMapper orderResponseMapper;
    
    @Operation(
        summary = "Create order from authenticated user's cart",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "409", description = "Insufficient stock"),
        @ApiResponse(responseCode = "422", description = "Cart is empty")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto> selfCreateOrder() {
        Order order = orderService.selfCreateOrder();
        OrderResponse response = orderResponseMapper.mapToDto(order);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(order.getId())
            .toUri();
        
        return ResponseEntity.created(location)
                             .body(new ApiResponseDto("Created", response));
    }
    
    @Operation(
        summary = "Get authenticated user's order by id",
        security = {@SecurityRequirement(name = "bearerAuth")},
        parameters = {
            @Parameter(name = "orderId", description = "Order id", required = true)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "400", description = "Invalid id"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("{orderId}")
    public ResponseEntity<ApiResponseDto> selfGetOrderById(
        @PathVariable @Positive long orderId
    ) {
        Order order = orderService.selfFindByIdWithItems(orderId);
        OrderResponse response = orderResponseMapper.mapToDto(order);
        return ResponseEntity.ok(new ApiResponseDto("Found", response));
    }
    
    @Operation(
        summary = "Get all authenticated user's orders",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto> selfGetAllOrders() {
        List<Order> orders = orderService.selfFindAllWithItems();
        List<OrderResponse> response = orders.stream().map(orderResponseMapper::mapToDto).toList();
        return ResponseEntity.ok(new ApiResponseDto("Found", response));
    }
    
}
