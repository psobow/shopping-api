package com.sobow.shopping.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.exceptions.OverDecrementException;
import com.sobow.shopping.utils.TestFixtures;
import org.junit.jupiter.api.Test;

public class ProductTests {
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Test
    public void setAvailableQty_should_ThrowOverDecrement_when_NewQtyBelowZero() {
        Product product = fixtures.productEntity();
        
        assertThrows(OverDecrementException.class, () -> product.setAvailableQty(-1));
    }
    
}
