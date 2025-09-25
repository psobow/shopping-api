package com.sobow.shopping.controllers.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.dto.CartItemResponse;
import com.sobow.shopping.domain.cart.dto.CartItemUpdateRequest;
import com.sobow.shopping.domain.cart.dto.CartResponse;
import com.sobow.shopping.exceptions.CartItemAlreadyExistsException;
import com.sobow.shopping.mappers.cart.CartItemResponseMapper;
import com.sobow.shopping.mappers.cart.CartResponseMapper;
import com.sobow.shopping.services.CartService;
import com.sobow.shopping.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CartService cartService;
    
    @MockitoBean
    private CartResponseMapper cartResponseMapper;
    
    @MockitoBean
    private CartItemResponseMapper cartItemResponseMapper;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String CART_PATH_BY_USER_ID = "/api/users/{userId}/cart";
    private static final String ITEMS_PATH_BY_CART_ID = "/api/carts/{cartId}/items";
    private static final String ITEMS_PATH_BY_CART_ID_AND_ITEM_ID = "/api/carts/{cartId}/items/{itemId}";
    
    @Nested
    @DisplayName("createOrGetCart")
    class createOrGetCart {
        
        @Test
        public void createOrGetCart_should_Return200_when_CartAlreadyExists() throws Exception {
            // Given
            long userId = fixtures.userId();
            Cart cart = fixtures.cartEntity();
            CartResponse response = fixtures.cartResponse();
            
            when(cartService.existsByUserProfile_UserId(userId)).thenReturn(true);
            when(cartService.createOrGetCart(userId)).thenReturn(cart);
            when(cartResponseMapper.mapToDto(cart)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(put(CART_PATH_BY_USER_ID, userId))
                   .andExpect(status().isOk())
                   .andExpect(header().doesNotExist(HttpHeaders.LOCATION))
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data").exists());
        }
        
        @Test
        public void createOrGetCart_should_Return201_when_CartCreated() throws Exception {
            // Given
            long userId = fixtures.userId();
            Cart cart = fixtures.cartEntity();
            CartResponse response = fixtures.cartResponse();
            
            when(cartService.existsByUserProfile_UserId(userId)).thenReturn(false);
            when(cartService.createOrGetCart(userId)).thenReturn(cart);
            when(cartResponseMapper.mapToDto(cart)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(put(CART_PATH_BY_USER_ID, userId))
                   .andExpect(status().isCreated())
                   .andExpect(header().exists(HttpHeaders.LOCATION))
                   .andExpect(jsonPath("$.message").value("Created"))
                   .andExpect(jsonPath("$.data").exists());
        }
        
        @Test
        public void createOrGetCart_should_Return400_when_UserIdIsNotPositive() throws Exception {
            // When & Then
            mockMvc.perform(put(CART_PATH_BY_USER_ID, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("deleteCart")
    class deleteCart {
        
        @Test
        public void deleteCart_should_Return204_when_CartDeleted() throws Exception {
            // When & Then
            mockMvc.perform(delete(CART_PATH_BY_USER_ID, fixtures.userId()))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        public void deleteCart_should_Return400_when_UserIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(delete(CART_PATH_BY_USER_ID, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("createCartItem")
    class createCartItem {
        
        @Test
        public void createCartItem_should_Return201_when_CartItemCreated() throws Exception {
            // Given
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            CartItem item = fixtures.cartItemEntity();
            CartItemResponse response = fixtures.cartItemResponse();
            
            when(cartService.createCartItem(fixtures.cartId(), request)).thenReturn(item);
            when(cartItemResponseMapper.mapToDto(item)).thenReturn(response);
            
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(ITEMS_PATH_BY_CART_ID, fixtures.cartId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isCreated())
                   .andExpect(jsonPath("$.message").value("Created"));
        }
        
        @Test
        public void createCartItem_should_Return409_when_CartItemAlreadyExists() throws Exception {
            // Given
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(cartService.createCartItem(fixtures.cartId(), request))
                .thenThrow(new CartItemAlreadyExistsException(fixtures.cartId(), fixtures.productId()));
            
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(ITEMS_PATH_BY_CART_ID, fixtures.cartId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isConflict());
        }
        
        @Test
        public void createCartItem_should_Return400_when_CartIdLessThanOne() throws Exception {
            // Given
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(ITEMS_PATH_BY_CART_ID, fixtures.invalidId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void createCartItem_should_Return400_when_RequestBodyInvalid() throws Exception {
            // Given
            CartItemCreateRequest request = new CartItemCreateRequest(0L, 0);
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(ITEMS_PATH_BY_CART_ID, fixtures.invalidId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("updateCartItemQty")
    class updateCartItemQty {
        
        @Test
        public void updateCartItemQty_should_Return200_when_CartItemUpdated() throws Exception {
            // Given
            CartItemUpdateRequest request = fixtures.cartItemUpdateRequest();
            CartItem item = fixtures.cartItemEntity();
            CartItemResponse response = fixtures.cartItemResponse();
            
            when(cartService.updateCartItemQty(fixtures.cartId(), fixtures.cartItemId(), request)).thenReturn(item);
            when(cartItemResponseMapper.mapToDto(item)).thenReturn(response);
            
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(ITEMS_PATH_BY_CART_ID_AND_ITEM_ID, fixtures.cartId(), fixtures.cartItemId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Updated"));
        }
        
        @Test
        public void updateCartItemQty_should_Return400_when_CartIdLessThanOne() throws Exception {
            // Given
            CartItemUpdateRequest request = fixtures.cartItemUpdateRequest();
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(ITEMS_PATH_BY_CART_ID_AND_ITEM_ID, fixtures.invalidId(), fixtures.cartItemId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateCartItemQty_should_Return400_when_ItemIdLessThanOne() throws Exception {
            // Given
            CartItemUpdateRequest request = fixtures.cartItemUpdateRequest();
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(ITEMS_PATH_BY_CART_ID_AND_ITEM_ID, fixtures.cartId(), fixtures.invalidId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateCartItemQty_should_Return400_when_RequestBodyInvalid() throws Exception {
            // Given
            CartItemUpdateRequest request = new CartItemUpdateRequest(-1);
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(ITEMS_PATH_BY_CART_ID_AND_ITEM_ID, fixtures.cartId(), fixtures.cartItemId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("deleteCartItem")
    class deleteCartItem {
        
        @Test
        public void deleteCartItem_should_Return204_when_CartItemDeleted() throws Exception {
            // When & Then
            mockMvc.perform(delete(ITEMS_PATH_BY_CART_ID_AND_ITEM_ID, fixtures.cartId(), fixtures.cartItemId()))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        public void deleteCartItem_should_Return400_when_CartIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(delete(ITEMS_PATH_BY_CART_ID_AND_ITEM_ID, fixtures.invalidId(), fixtures.cartItemId()))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void deleteCartItem_should_Return400_when_CartItemIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(delete(ITEMS_PATH_BY_CART_ID_AND_ITEM_ID, fixtures.cartId(), fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("deleteAllCartItems")
    class deleteAllCartItems {
        
        @Test
        public void deleteAllCartItems_should_Return204_when_CartItemsDeleted() throws Exception {
            // When & Then
            mockMvc.perform(delete(ITEMS_PATH_BY_CART_ID, fixtures.cartId()))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        public void deleteAllCartItems_should_Return400_when_CartIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(delete(ITEMS_PATH_BY_CART_ID, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
    }
}