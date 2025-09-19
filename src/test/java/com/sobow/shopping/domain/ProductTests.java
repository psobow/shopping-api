package com.sobow.shopping.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.exceptions.InvalidPriceException;
import com.sobow.shopping.exceptions.OverDecrementException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ProductTests {
    
    @Test
    public void setAvailableQty_should_ThrowOverDecrement_when_NewQtyBelowZero() {
        Product product = new Product();
        
        assertThrows(OverDecrementException.class, () -> product.setAvailableQty(-5));
    }
    
    @Test
    public void setPrice_should_ThrowInvalidPrice_when_NewPriceBelowZero() {
        Product product = new Product();
        
        assertThrows(InvalidPriceException.class, () -> product.setPrice(new BigDecimal(-5)));
    }
}
