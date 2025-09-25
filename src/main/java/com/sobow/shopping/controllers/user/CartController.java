package com.sobow.shopping.controllers.user;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.dto.CartItemResponse;
import com.sobow.shopping.domain.cart.dto.CartItemUpdateRequest;
import com.sobow.shopping.domain.cart.dto.CartResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    @Qualifier("cartResponseMapper")
    private final Mapper<Cart, CartResponse> cartResponseMapper;
    @Qualifier("cartItemResponseMapper")
    private final Mapper<CartItem, CartItemResponse> cartItemResponseMapper;
    
    @PutMapping("/users/{userId}/cart")
    public ResponseEntity<ApiResponse> createOrGetCart(@PathVariable @Positive long userId) {
        boolean cartExists = cartService.existsByUserProfile_UserId(userId);
        Cart cart = cartService.createOrGetCart(userId);
        CartResponse response = cartResponseMapper.mapToDto(cart);
        ApiResponse body = new ApiResponse(cartExists ? "Found" : "Created", response);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().buildAndExpand(userId).toUri();
        return cartExists ? ResponseEntity.ok().body(body)
                          : ResponseEntity.created(location).body(body);
    }
    
    @DeleteMapping("/users/{userId}/cart")
    public ResponseEntity<Void> deleteCart(@PathVariable @Positive long userId) {
        cartService.removeCart(userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/carts/{cartId}/items")
    public ResponseEntity<ApiResponse> createCartItem(
        @PathVariable @Positive long cartId,
        @RequestBody @Valid CartItemCreateRequest request
    ) {
        CartItem cartItem = cartService.createCartItem(cartId, request);
        CartItemResponse response = cartItemResponseMapper.mapToDto(cartItem);
        return ResponseEntity.status(HttpStatus.CREATED)
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
