package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.entities.Cart;
import com.sobow.shopping.domain.entities.CartItem;
import com.sobow.shopping.domain.responses.CartItemResponse;
import com.sobow.shopping.domain.responses.CartResponse;
import com.sobow.shopping.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CartResponseMapper implements Mapper<Cart, CartResponse> {
    
    private final Mapper<CartItem, CartItemResponse> cartItemResponseMapper;
    
    @Override
    public Cart mapToEntity(CartResponse cartResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public CartResponse mapToDto(Cart cart) {
        return new CartResponse(
            cart.getId(),
            cart.getTotalPrice(),
            cart.getCartItems().stream().map(cartItemResponseMapper::mapToDto).toList()
        );
    }
}
