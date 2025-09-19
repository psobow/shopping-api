package com.sobow.shopping.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.CartItemResponse;
import com.sobow.shopping.domain.cart.CartResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
class CartControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CartService cartService;
    
    @MockitoBean
    private Mapper<Cart, CartResponse> cartResponseMapper;
    
    @MockitoBean
    private Mapper<CartItem, CartItemResponse> cartItemResponseMapper;
    
    private static final String CART_PATH_BY_USER_ID = "/api/users/{userId}/cart";
    
    @Nested
    @DisplayName("createOrGetCart")
    class createOrGetCart {
        
        @Test
        public void createOrGetCart_should_Return200_when_CartAlreadyExists() throws Exception {
            // Given
            long userId = 123L;
            Cart cart = Mockito.mock(Cart.class);
            
            when(cartService.existsByUserProfile_UserId(userId)).thenReturn(true);
            when(cartService.createOrGetCart(userId)).thenReturn(cart);
            // We don't assert DTO fields here, so null is fine
            when(cartResponseMapper.mapToDto(cart)).thenReturn(null);
            
            // When & Then
            mockMvc.perform(put(CART_PATH_BY_USER_ID, userId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(status().isOk())
                   .andExpect(header().doesNotExist("Location"))
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.message", equalTo("Found")));
        }
        
        @Test
        public void createOrGetCart_should_Return201_when_CartCreated() throws Exception {
            // Given
            long userId = 123L;
            Cart cart = Mockito.mock(Cart.class);
            
            when(cartService.existsByUserProfile_UserId(userId)).thenReturn(false);
            when(cartService.createOrGetCart(userId)).thenReturn(cart);
            when(cartResponseMapper.mapToDto(cart)).thenReturn(null);
            
            // When & Then
            mockMvc.perform(put(CART_PATH_BY_USER_ID, userId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(status().isCreated())
                   .andExpect(header().exists("Location"))
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.message", equalTo("Created")));
        }
        
        @Test
        public void createOrGetCart_should_Return400_when_UserIdIsNotPositive() throws Exception {
            // Given
            long userId = -1L;
            
            // When & Then
            mockMvc.perform(put(CART_PATH_BY_USER_ID, userId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("deleteCart")
    class deleteCart {
        
        @Test
        public void deleteCart_should_Return204_when_CartDeleted() {
            fail("implement more tests");
        }
        
        @Test
        public void deleteCart_should_Return400_when_UserIdLessThanOne() throws Exception {
            fail("implement more tests");
        }
    }
    
    @Nested
    @DisplayName("getCart")
    class getCart {
        
        @Test
        public void getCart_should_Return200_when_CartExists() {
            fail("implement more tests");
        }
        
        @Test
        public void getCart_should_Return400_when_CartIdLessThanOne() {
            fail("implement more tests");
        }
    }
    
    @Nested
    @DisplayName("createCartItem")
    class createCartItem {
        
        @Test
        public void createCartItem_should_Return201_when_CartItemCreated() {
            fail("implement more tests");
        }
        
        @Test
        public void createCartItem_should_Return409_when_CartItemAlreadyExists() {
            fail("implement more tests");
        }
        
        @Test
        public void createCartItem_should_Return400_when_CartIdLessThanOne() {
            fail("implement more tests");
        }
        
        @Test
        public void createCartItem_should_Return400_when_RequestBodyInvalid() {
            fail("implement more tests");
        }
    }
    
    @Nested
    @DisplayName("updateCartItemQty")
    class updateCartItemQty {
        
        @Test
        public void updateCartItemQty_should_Return200_when_CartItemUpdated() {
            fail("implement more tests");
        }
        
        @Test
        public void updateCartItemQty_should_Return400_when_CartIdLessThanOne() {
            fail("implement more tests");
        }
        
        @Test
        public void updateCartItemQty_should_Return400_when_ItemIdLessThanOne() {
            fail("implement more tests");
        }
        
        @Test
        public void updateCartItemQty_should_Return400_when_RequestBodyInvalid() {
            fail("implement more tests");
        }
    }
    
    @Nested
    @DisplayName("deleteCartItem")
    class deleteCartItem {
        
        @Test
        public void deleteCartItem_should_Return204_when_CartItemDeleted() {
            fail("implement more tests");
        }
        
        @Test
        public void deleteCartItem_should_Return400_when_CartIdLessThanOne() {
            fail("implement more tests");
        }
        
        @Test
        public void deleteCartItem_should_Return400_when_CartItemIdLessThanOne() {
            fail("implement more tests");
        }
    }
    
    @Nested
    @DisplayName("deleteAllCartItems")
    class deleteAllCartItems {
        
        @Test
        public void deleteAllCartItems_should_Return204_when_CartItemsDeleted() {
            fail("implement more tests");
        }
        
        @Test
        public void deleteAllCartItems_should_Return400_when_CartIdLessThanOne() {
            fail("implement more tests");
        }
    }
}