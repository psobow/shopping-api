package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.responses.ApiResponse;
import com.sobow.shopping.domain.responses.CartResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CartService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
    
    private final CartService cartService;
    private final Mapper<Cart, CartResponse> cartResponseMapper;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCart(@PathVariable @Positive long id) {
        Cart cart = cartService.findCartById(id);
        CartResponse response = cartResponseMapper.mapToDto(cart);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
}
