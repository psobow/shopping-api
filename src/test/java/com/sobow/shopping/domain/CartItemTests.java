package com.sobow.shopping.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.exceptions.OverDecrementException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CartItemTests {
    
    private CartItem itemWith(String unitPrice, int qty) {
        Product p = new Product();
        p.setPrice(new BigDecimal(unitPrice));
        CartItem ci = new CartItem();
        ci.setProduct(p);
        ci.setQuantity(qty);
        return ci;
    }
    
    private CartItem itemWith(Product p, Integer qty) {
        CartItem ci = new CartItem();
        ci.setProduct(p);
        ci.setQuantity(qty);
        return ci;
    }
    
    private Product productWithAvailableQty(int availableQty) {
        Product p = new Product();
        p.setId(1L);
        p.setAvailableQuantity(availableQty);
        p.setPrice(new BigDecimal("1.23"));
        return p;
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
    @DisplayName("incrementQuantity")
    class incrementQuantity {
        
        @Test
        public void incrementQuantity_should_ThrowIllegalState_when_ProductIsNull() {
            Product p = null;
            CartItem item = itemWith(p, 0);
            assertThrows(IllegalStateException.class, () -> item.incrementQuantity(5));
        }
        
        @Test
        public void incrementQuantity_should_ThrowIllegalArgument_when_DeltaNotPositive() {
            Product p = productWithAvailableQty(10);
            CartItem item = itemWith(p, 1);
            assertThrows(IllegalArgumentException.class, () -> item.incrementQuantity(0));
            assertThrows(IllegalArgumentException.class, () -> item.incrementQuantity(-1));
        }
        
        @Test
        public void incrementQuantity_should_ThrowInsufficientStock_when_NewQtyExceedsAvailable() {
            Product p = productWithAvailableQty(4);
            CartItem item = itemWith(p, 3);
            
            assertThrows(InsufficientStockException.class, () -> item.incrementQuantity(2)); // 3+2 > 4
        }
        
        @Test
        public void incrementQuantity_should_SetNewQuantity_when_NewQtyWithinAvailable() {
            Product p = productWithAvailableQty(10);
            CartItem item = itemWith(p, 2);
            
            int result = item.incrementQuantity(5);
            
            assertEquals(7, result);
            assertEquals(7, item.getQuantity());
        }
        
        @Test
        public void incrementQuantity_should_AllowBoundary_when_NewQtyEqualsAvailable() {
            
            Product p = productWithAvailableQty(5);
            CartItem item = itemWith(p, 2);
            
            int result = item.incrementQuantity(3);
            
            assertEquals(5, result);
            
            assertEquals(5, item.getQuantity());
        }
    }
    
    @Nested
    @DisplayName("decrementQuantity")
    class decrementQuantity {
        
        @Test
        public void decrementQuantity_should_ThrowIllegalState_when_ProductIsNull() {
            Product p = null;
            CartItem item = itemWith(p, 0);
            assertThrows(IllegalStateException.class, () -> item.decrementQuantity(5));
        }
        
        @Test
        public void decrementQuantity_should_ThrowIllegalArgument_when_DeltaNotPositive() {
            Product p = productWithAvailableQty(10);
            CartItem item = itemWith(p, 1);
            assertThrows(IllegalArgumentException.class, () -> item.decrementQuantity(0));
            assertThrows(IllegalArgumentException.class, () -> item.decrementQuantity(-1));
        }
        
        @Test
        public void decrementQuantity_should_ThrowOverDecrement_when_NewQtyBelowZero() {
            Product p = productWithAvailableQty(4);
            CartItem item = itemWith(p, 3);
            
            assertThrows(OverDecrementException.class, () -> item.decrementQuantity(5));
        }
        
        @Test
        public void decrementQuantity_should_SetNewQuantity_when_NewQtyPositive() {
            Product p = productWithAvailableQty(4);
            CartItem item = itemWith(p, 3);
            
            int result = item.decrementQuantity(1);
            
            assertEquals(2, result);
            assertEquals(2, item.getQuantity());
        }
        
        @Test
        public void decrementQuantity_should_AllowBoundary_when_NewQtyEqualsZero() {
            Product p = productWithAvailableQty(4);
            CartItem item = itemWith(p, 3);
            
            int result = item.decrementQuantity(3);
            
            assertEquals(0, result);
            assertEquals(0, item.getQuantity());
        }
    }
}
