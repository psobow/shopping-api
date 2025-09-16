package com.sobow.shopping.mappers.Impl;

import com.sobow.shopping.domain.entities.CartItem;
import com.sobow.shopping.domain.responses.CartItemResponse;
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
        return new CartItemResponse(item.getId(),
                                    item.getProduct().getId(),
                                    item.getCart().getId(),
                                    item.getQuantity(),
                                    item.getTotalPrice()
        );
    }
}
