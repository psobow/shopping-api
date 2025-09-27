package com.sobow.shopping.mappers.cart.Impl;

import com.sobow.shopping.controllers.cart.dto.CartResponse;
import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.mappers.cart.CartItemResponseMapper;
import com.sobow.shopping.mappers.cart.CartResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CartResponseMapperImpl implements CartResponseMapper {
    
    private final CartItemResponseMapper cartItemResponseMapper;
    
    @Override
    public CartResponse mapToDto(Cart cart) {
        return new CartResponse(
            cart.getId(),
            cart.getTotalPrice(),
            cart.getCartItems().stream().map(cartItemResponseMapper::mapToDto).toList()
        );
    }
}
