package com.sobow.shopping.mappers.cart.Impl;

import com.sobow.shopping.controllers.cart.dto.CartItemResponse;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.mappers.cart.CartItemResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class CartItemResponseMapperImpl implements CartItemResponseMapper {
    
    @Override
    public CartItemResponse mapToDto(CartItem item) {
        return CartItemResponse.builder()
                               .id(item.getId())
                               .requestedQty(item.getRequestedQty())
                               .productPrice(item.getProductPrice())
                               .totalItemPrice(item.getTotalPrice())
                               .productId(item.getProduct().getId())
                               .cartId(item.getCart().getId())
                               .build();
    }
}
