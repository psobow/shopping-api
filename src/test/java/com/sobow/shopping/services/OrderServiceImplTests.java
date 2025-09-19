package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.fail;

import com.sobow.shopping.repositories.OrderRepository;
import com.sobow.shopping.services.Impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTests {
    
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private ProductService productService;
    @Mock
    private CartService cartService;
    
    @InjectMocks
    private OrderServiceImpl underTest;
    
    @Nested
    @DisplayName("createOrder")
    class createOrder {
        
        @Test
        public void createOrder_should_CreateOrder_when_CartNotEmpty() {
            fail("implement me");
        }
        
        @Test
        public void createOrder_should_ThrowEntityNotFound_when_UserHasNoCart() {
            fail("implement me");
        }
        
        @Test
        public void createOrder_should_ThrowCartEmpty_when_CartEmpty() {
            fail("implement me");
        }
        
        @Test
        public void createOrder_should_ThrowInsufficientStock_when_StockChanged() {
            fail("implement me");
        }
        
        @Test
        public void createOrder_should_RemoveCart_when_OrderCreated() {
            fail("implement me");
        }
    }
}
