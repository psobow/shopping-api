package com.sobow.shopping.exceptions;

import com.sobow.shopping.domain.entities.Cart;
import java.util.Optional;

public class CartEmptyException extends RuntimeException {
    
    public CartEmptyException(Optional<Cart> optionalCart) {
        super("Cannot create order: cart " + optionalCart.map(Cart::getId)
                                                         .map(String::valueOf)
                                                         .orElse("") + " is empty.");
    }
}