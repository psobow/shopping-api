package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.CartItemResponse;
import com.sobow.shopping.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CartItemResponseMapper implements Mapper<CartItem, CartItemResponse> {
    
    @Override
    public CartItem mapToEntity(CartItemResponse cartItemResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public CartItemResponse mapToDto(CartItem item) {
        return CartItemResponse.builder()
                               .id(item.getId())
                               .requestedQty(item.getRequestedQty())
                               .totalItemPrice(item.getTotalPrice())
                               .productId(item.getProduct().getId())
                               .cartId(item.getCart().getId())
                               .build();
    }
}
