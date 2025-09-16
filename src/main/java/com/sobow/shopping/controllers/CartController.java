package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;
import com.sobow.shopping.domain.responses.ApiResponse;
import com.sobow.shopping.domain.responses.CartItemResponse;
import com.sobow.shopping.domain.responses.CartResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}")
public class CartController {
    
    private final CartService cartService;
    private final Mapper<Cart, CartResponse> cartResponseMapper;
    private final Mapper<CartItem, CartItemResponse> cartItemResponseMapper;
    
    @PutMapping("/users/{userId}/cart")
    public ResponseEntity<ApiResponse> createOrGetCartForUser(@PathVariable @Positive long userId) {
        // 201 when created
        // 200 when already exists and return existing
        Cart cart = cartService.createOrGetCartForUser(1L);
        CartResponse response = cartResponseMapper.mapToDto(cart);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponse("Created", response));
    }
    
    @DeleteMapping("/users/{userId}/cart")
    public ResponseEntity<Void> deleteCartForUser(@PathVariable @Positive long userId) {
        return null;
    }
    
    @GetMapping("/carts/{id}")
    public ResponseEntity<ApiResponse> getCart(@PathVariable @Positive long id) {
        Cart cart = cartService.findById(id);
        CartResponse response = cartResponseMapper.mapToDto(cart);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @PostMapping("/carts/{cartId}/items")
    public ResponseEntity<ApiResponse> createCartItem(
        @PathVariable @Positive long cartId,
        @RequestBody @Valid CartItemCreateRequest request
    ) {
        CartItem cartItem = cartService.createCartItem(cartId, request);
        CartItemResponse response = cartItemResponseMapper.mapToDto(cartItem);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{itemId}")
            .buildAndExpand(cartItem.getId())
            .toUri();
        return ResponseEntity.created(location)
                             .body(new ApiResponse("Created", response));
    }
    
    @PutMapping("/carts/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse> updateCartItemQty(
        @PathVariable @Positive long cartId,
        @PathVariable @Positive long itemId,
        @RequestBody @Valid CartItemUpdateRequest request
    ) {
        CartItem cartItem = cartService.updateCartItemQty(cartId, itemId, request);
        CartItemResponse response = cartItemResponseMapper.mapToDto(cartItem);
        
        return ResponseEntity.ok(new ApiResponse("Updated", response));
    }
    
    @DeleteMapping("/carts/{cartId}/items/{itemId}")
    public ResponseEntity<Void> deleteCartItem(
        @PathVariable @Positive long cartId,
        @PathVariable @Positive long itemId
    ) {
        cartService.removeCartItem(cartId, itemId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/carts/{cartId}/items")
    public ResponseEntity<Void> deleteAllCartItems(
        @PathVariable @Positive long cartId
    ) {
        cartService.removeAllCartItems(cartId);
        return ResponseEntity.noContent().build();
    }
}
