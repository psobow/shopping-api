package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.fail;

import com.sobow.shopping.repositories.CartItemRepository;
import com.sobow.shopping.repositories.CartRepository;
import com.sobow.shopping.services.Impl.CartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTests {
    
    @Mock
    private CartRepository cartRepository;
    
    @Mock
    private CartItemRepository cartItemRepository;
    
    @InjectMocks
    private CartServiceImpl underTest;
    
    @Nested
    @DisplayName("addCartItem")
    class addCartItem {
        
        @Nested
        @DisplayName("addCartItem_ErrorsPath")
        class addCartItem_ErrorsPath {
            
            @Test
            public void addCartItem_should_ThrowNotFound_when_CartDoesNotExist() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_ThrowOutOfStock_when_AvailableIsZero() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_ThrowInsufficientStock_when_RequestExceedsAvailable() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_ThrowInsufficientStock_when_RequestExceedsAvailableConsideringAlreadyInCart() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_NotMutateCart_when_ExceptionIsThrown() {
                fail("Implement me");
            }
        }
        
        @Nested
        @DisplayName("addCartItem_CreatePath")
        class addCartItem_CreatePath {
            
            @Test
            public void addCartItem_should_CreateNewCartItem_and_AddToCart_when_ProductNotInCart() {
                fail("Implement me");
            }
        }
        
        @Nested
        @DisplayName("addCartItem_UpdatePath")
        class addCartItem_UpdatePath {
            
            @Test
            public void addCartItem_should_UpdateCartItemQty_when_CartItemAlreadyInCart() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_NotDuplicateCartItem_when_CartItemAlreadyInCart() {
                fail("Implement me");
            }
        }
        
        @Nested
        @DisplayName("addCartItem_Pricing")
        class addCartItem_Pricing {
            
            @Test
            public void addCartItem_should_CalculateTotalCartItemPrice_asNewQtyTimesProductPrice() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_SetPrice_with_TwoFractionDigits() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_IncreaseCartTotalPrice_ByRequestedProductsPrice_when_CartItemAlreadyInCart() {
                fail(
                    "Implement me");
            }
            
            @Test
            public void addCartItem_should_IncreaseCartTotalPrice_ByRequestedProductsPrice_when_CartItemIsNew() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_NotChangeCartTotalPrice_when_ExceptionIsThrown() {
                fail("Implement me");
            }
        }
    }
}
