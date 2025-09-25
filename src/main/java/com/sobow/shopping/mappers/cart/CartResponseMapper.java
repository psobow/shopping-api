package com.sobow.shopping.mappers.cart;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemResponse;
import com.sobow.shopping.domain.cart.dto.CartResponse;
import com.sobow.shopping.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("cartResponseMapper")
public class CartResponseMapper implements Mapper<Cart, CartResponse> {
    
    @Qualifier("cartItemResponseMapper")
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
