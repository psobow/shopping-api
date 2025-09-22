package com.sobow.shopping.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.exceptions.OverDecrementException;
import com.sobow.shopping.utils.TestFixtures;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CartItemTests {
    
    private final TestFixtures fixtures = new TestFixtures();
    
    private CartItem itemWith(String unitPrice, int qty) {
        Product p = new Product(null, null, null, new BigDecimal(unitPrice), qty);
        CartItem ci = new CartItem(p, qty);
        return ci;
    }
    
    
    @Nested
    @DisplayName("getTotalPrice")
    class getTotalPrice {
        
        @Test
        void getTotalPrice_should_ReturnZero_when_QuantityIsZero() {
            CartItem ci = itemWith("12.34", 0);
            assertEquals(new BigDecimal("0.00"), ci.getTotalPrice());
        }
        
        @Test
        void getTotalPrice_should_MultiplyUnitPriceByQuantity_when_WholeNumbers() {
            CartItem ci = itemWith("10.00", 3);
            assertEquals(new BigDecimal("30.00"), ci.getTotalPrice());
        }
        
        @Test
        void getTotalPrice_should_RoundHalfUpToTwoDecimals_when_PrecisionExceedsTwo() {
            CartItem ci = itemWith("19.999", 1);
            assertEquals(new BigDecimal("20.00"), ci.getTotalPrice());
        }
        
        @Test
        void getTotalPrice_should_HonorTwoDecimalScale_when_UnitPriceHasNoDecimal() {
            CartItem ci = itemWith("3", 1);
            assertEquals(new BigDecimal("3.00"), ci.getTotalPrice());
        }
    }
    
    @Nested
    @DisplayName("setRequestedQty")
    class setRequestedQty {
        
        @Test
        public void setRequestedQty_should_ThrowInsufficientStock_when_RequestedQtyExceedsAvailable() {
            CartItem item = fixtures.withAvailableQty(4)
                                    .cartItemEntity();
            
            assertThrows(InsufficientStockException.class, () -> item.setRequestedQty(6));
        }
        
        @Test
        public void setRequestedQty_should_ThrowOverDecrement_when_NewQtyBelowZero() {
            CartItem item = fixtures.withAvailableQty(4)
                                    .cartItemEntity();
            
            assertThrows(OverDecrementException.class, () -> item.setRequestedQty(-5));
        }
        
        @Test
        public void setRequestedQty_should_SetNewQuantity_when_RequestedQtyWithinAvailable() {
            CartItem item = fixtures.withAvailableQty(4)
                                    .cartItemEntity();
            
            item.setRequestedQty(1);
            
            assertEquals(1, item.getRequestedQty());
        }
        
        @Test
        public void setRequestedQty_should_AllowBoundary_when_requestedQtyEqualsAvailable() {
            CartItem item = fixtures.withAvailableQty(4)
                                    .cartItemEntity();
            
            item.setRequestedQty(4);
            
            assertEquals(4, item.getRequestedQty());
        }
        
        @Test
        public void setRequestedQty_should_AllowBoundary_when_RequestedQtyEqualsZero() {
            CartItem item = fixtures.withAvailableQty(4)
                                    .cartItemEntity();
            
            item.setRequestedQty(0);
            
            assertEquals(0, item.getRequestedQty());
        }
    }
}
