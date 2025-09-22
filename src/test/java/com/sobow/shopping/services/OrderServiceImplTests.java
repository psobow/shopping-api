package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.exceptions.CartEmptyException;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.repositories.OrderRepository;
import com.sobow.shopping.services.Impl.OrderServiceImpl;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
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
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("createOrder")
    class createOrder {
        
        @Test
        public void createOrder_should_CreateOrder_and_RemoveCart_when_CartNotEmpty() {
            // Given
            User user = fixtures.userEntity();
            UserProfile userProfile = fixtures.userProfileEntity();
            Cart cart = fixtures.cartEntity();
            CartItem cartItem = fixtures.cartItemEntity();
            
            user.setProfileAndLink(userProfile);
            userProfile.setCartAndLink(cart);
            cart.addCartItemAndLink(cartItem);
            
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            when(cartService.findByUserIdWithItems(fixtures.userId())).thenReturn(cart);
            
            // When
            Order result = underTest.createOrder(fixtures.userId());
            
            // Then
            assertThat(userProfile.getOrders()).contains(result).hasSize(1);
            verify(productService, times(1)).lockForOrder(anyList());
            assertThat(userProfile.getCart()).isNull();
        }
        
        @Test
        public void createOrder_should_ThrowEntityNotFound_when_UserHasNoCart() {
            // Given
            when(cartService.findByUserIdWithItems(fixtures.userId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.createOrder(fixtures.userId()));
        }
        
        @Test
        public void createOrder_should_ThrowCartEmpty_when_CartEmpty() {
            // Given
            Cart cart = fixtures.cartEntity();
            when(cartService.findByUserIdWithItems(fixtures.userId())).thenReturn(cart);
            
            // When & Then
            assertThrows(CartEmptyException.class, () -> underTest.createOrder(fixtures.userId()));
        }
        
        @Test
        public void createOrder_should_ThrowInsufficientStock_when_StockChanged() {
            // Given
            User user = fixtures.userEntity();
            UserProfile userProfile = fixtures.userProfileEntity();
            Cart cart = fixtures.cartEntity();
            CartItem cartItem = fixtures.cartItemEntity();
            
            user.setProfileAndLink(userProfile);
            userProfile.setCartAndLink(cart);
            cart.addCartItemAndLink(cartItem);
            
            Product product = cartItem.getProduct();
            product.setAvailableQty(0);
            
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            when(cartService.findByUserIdWithItems(fixtures.userId())).thenReturn(cart);
            
            // When & Then
            assertThrows(InsufficientStockException.class, () -> underTest.createOrder(fixtures.userId()));
        }
    }
}
