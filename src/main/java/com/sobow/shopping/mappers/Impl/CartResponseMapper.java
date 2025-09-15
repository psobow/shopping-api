package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.responses.CartResponse;
import com.sobow.shopping.mappers.Mapper;

public class CartResponseMapper implements Mapper<Cart, CartResponse> {
    
    @Override
    public Cart mapToEntity(CartResponse cartResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public CartResponse mapToDto(Cart cart) {
        return new CartResponse(
            cart.getId(),
            cart.getCartItems().stream().map(CartItem::getId).toList(),
            cart.getTotalPrice()
        );
    }
}
