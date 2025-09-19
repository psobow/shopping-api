package com.sobow.shopping.controllers;

import static org.hamcrest.Matchers.equalTo;
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
        public void createOrGetCart_should_Return201WithDto_when_CartDoesNotExist() throws Exception {
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
    
    }
}