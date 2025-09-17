package com.sobow.shopping.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sobow.shopping.domain.entities.Product;
import com.sobow.shopping.exceptions.OverDecrementException;
import org.junit.jupiter.api.Test;

public class ProductTests {
    
    @Test
    public void setAvailableQty_should_ThrowOverDecrement_when_NewQtyBelowZero() {
        Product product = new Product();
        
        assertThrows(OverDecrementException.class, () -> product.setAvailableQty(-5));
    }
}
