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
public class CartController {
    
    private final CartService cartService;
    private final CartResponseMapper cartResponseMapper;
    private final CartItemResponseMapper cartItemResponseMapper;
    
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
    
    @DeleteMapping
    public ResponseEntity<Void> selfRemoveCart() {
        cartService.selfRemoveCart();
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/items")
    public ResponseEntity<ApiResponseDto> selfCreateCartItem(
        @RequestBody @Valid CartItemCreateRequest request
    ) {
        CartItem cartItem = cartService.selfCreateCartItem(request);
        CartItemResponse response = cartItemResponseMapper.mapToDto(cartItem);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponseDto("Created", response));
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponseDto> selfUpdateCartItemQty(
        @PathVariable @Positive long itemId,
        @RequestBody @Valid CartItemUpdateRequest request
    ) {
        CartItem cartItem = cartService.selfUpdateCartItemQty(itemId, request);
        CartItemResponse response = cartItemResponseMapper.mapToDto(cartItem);
        
        return ResponseEntity.ok(new ApiResponseDto("Updated", response));
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> selfRemoveCartItem(
        @PathVariable @Positive long itemId
    ) {
        cartService.selfRemoveCartItem(itemId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/items")
    public ResponseEntity<Void> selfRemoveAllCartItems() {
        cartService.selfRemoveAllCartItems();
        return ResponseEntity.noContent().build();
    }
}
