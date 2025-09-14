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
    
    @Mock
    private ProductService productService;
    
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
            public void addCartItem_should_ThrowNotFound_when_ProductDoesNotExist() {
                fail("Implement me");
            }
            
            @Test
            public void addCartItem_should_ThrowInsufficientStock_when_RequestedQtyExceedsAvailable() {
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
            public void addCartItem_should_CreateNewCartItem_and_AddToCart_when_CartItemNotInCart() {
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
    }
    
    @Nested
    @DisplayName("removeCartItem")
    class removeCartItem {
        
        @Test
        public void removeCartItem_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            fail("Implement me");
        }
        
        @Test
        public void removeCartItem_should_RemoveItem_when_ItemExistsInCart() {
            fail("Implement me");
        }
        
        @Test
        public void removeCartItem_should_BeIdempotent_when_CalledTwice_SecondCallThrowsNotFound() {
            fail("Implement me");
        }
    }
    
    @Nested
    @DisplayName("incrementCartItemQty")
    class incrementCartItemQty {
        
        @Test
        public void incrementCartItemQty_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            fail("Implement me");
        }
        
        @Test
        public void incrementCartItemQty_should_IncrementItemQty_when_NewQtyWithinAvailableStock() {
            fail("Implement me");
        }
    }
    
    @Nested
    @DisplayName("decrementCartItemQty")
    class decrementCartItemQty {
        
        @Test
        public void decrementCartItemQty_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            fail("Implement me");
        }
        
        @Test
        public void decrementCartItemQty_should_DecrementItemQty_when_NewQtyPositiveOrZero() {
            fail("Implement me");
        }
    }
    
    @Nested
    @DisplayName("removeAllCartItems")
    class removeAllCartItems {
        
        @Test
        public void removeAllCartItems_should_ThrowNotFound_when_CartDoesNotExist() {
            fail("Implement me");
        }
        
        @Test
        public void removeAllCartItems_should_RemoveAllCartItems_when_CartExists() {
            fail("Implement me");
        }
    }
}
