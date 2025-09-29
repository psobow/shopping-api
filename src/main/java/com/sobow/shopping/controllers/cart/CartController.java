package com.sobow.shopping.controllers.cart;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.controllers.cart.dto.CartItemResponse;
import com.sobow.shopping.controllers.cart.dto.CartItemUpdateRequest;
import com.sobow.shopping.controllers.cart.dto.CartResponse;
import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.mappers.cart.CartItemResponseMapper;
import com.sobow.shopping.mappers.cart.CartResponseMapper;
import com.sobow.shopping.services.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users/me/cart")
@Tag(
    name = "Cart Controller",
    description = "API to manage cart by authenticated user"
)
public class CartController {
    
    private final CartService cartService;
    private final CartResponseMapper cartResponseMapper;
    private final CartItemResponseMapper cartItemResponseMapper;
    
    @Operation(
        summary = "Create or get authenticated user's cart",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping
    public ResponseEntity<ApiResponseDto> selfCreateOrGetCart() {
        boolean cartExists = cartService.exists();
        
        Cart cart = cartService.selfCreateOrGetCart();
        CartResponse response = cartResponseMapper.mapToDto(cart);
        
        URI location = URI.create("/users/me/cart");
        ApiResponseDto body = new ApiResponseDto(cartExists ? "Found" : "Created", response);
        return cartExists ? ResponseEntity.ok().body(body)
                          : ResponseEntity.created(location).body(body);
    }
    
    @Operation(
        summary = "Delete authenticated user's cart",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping
    public ResponseEntity<Void> selfRemoveCart() {
        cartService.selfRemoveCart();
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Create cart item",
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "Request", value = """
                    {
                        "productId": 1,
                        "requestedQty": 2
                    }
                    """)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/items")
    public ResponseEntity<ApiResponseDto> selfCreateCartItem(
        @RequestBody @Valid CartItemCreateRequest request
    ) {
        CartItem cartItem = cartService.selfCreateCartItem(request);
        CartItemResponse response = cartItemResponseMapper.mapToDto(cartItem);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponseDto("Created", response));
    }
    
    @Operation(
        summary = "Update cart item",
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "Request", value = """
                    {
                      "requestedQty": 3
                    }
                    """)
            )
        )
    )
    @Parameters({
        @Parameter(name = "itemId", required = true, description = "Cart item ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponseDto> selfUpdateCartItemQty(
        @PathVariable @Positive long itemId,
        @RequestBody @Valid CartItemUpdateRequest request
    ) {
        CartItem cartItem = cartService.selfUpdateCartItemQty(itemId, request);
        CartItemResponse response = cartItemResponseMapper.mapToDto(cartItem);
        
        return ResponseEntity.ok(new ApiResponseDto("Updated", response));
    }
    
    @Operation(
        summary = "Delete cart item",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameters({
        @Parameter(name = "itemId", required = true, description = "Cart item ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> selfRemoveCartItem(
        @PathVariable @Positive long itemId
    ) {
        cartService.selfRemoveCartItem(itemId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Delete all cart items",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/items")
    public ResponseEntity<Void> selfRemoveAllCartItems() {
        cartService.selfRemoveAllCartItems();
        return ResponseEntity.noContent().build();
    }
}
