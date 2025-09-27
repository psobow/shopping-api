package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.order.OrderStatus;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.exceptions.CartEmptyException;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.repositories.OrderRepository;
import com.sobow.shopping.services.Impl.OrderServiceImpl;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
    @Mock
    private CurrentUserService currentUserService;
    
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
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            UserProfile userProfile = fixtures.userProfileEntity();
            Cart cart = fixtures.cartEntity();
            CartItem cartItem = fixtures.cartItemEntity();
            
            ReflectionTestUtils.setField(cartItem.getProduct(), "id", fixtures.productId());
            user.setProfileAndLink(userProfile);
            userProfile.setCartAndLink(cart);
            cart.addCartItemAndLink(cartItem);
            
            // Snapshots
            List<Long> cartProductsIdsBefore = cart.getProductsId();
            Set<CartItem> cartItemsBefore = new HashSet<>(cart.getCartItems());
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            when(cartService.findByUserIdWithItems(fixtures.userId())).thenReturn(cart);
            
            // When
            Order result = underTest.selfCreateOrder();
            
            // Then
            // Assert: services loaded the profile and cart
            verify(userProfileService).findByUserId(fixtures.userId());
            verify(cartService).findByUserIdWithItems(fixtures.userId());
            
            // Assert: product stock was locked for EXACT products from cart
            ArgumentCaptor<List<Long>> idsCaptor = ArgumentCaptor.forClass(List.class);
            verify(productService, times(1)).lockForOrder(idsCaptor.capture());
            assertThat(idsCaptor.getValue()).containsExactlyElementsOf(cartProductsIdsBefore);
            
            // Assert: order is NEW
            assertThat(result.getStatus()).isEqualTo(OrderStatus.NEW);
            
            // Assert: order items mirror cart items (product, brand, price, qty)
            assertThat(result.getOrderItems()).hasSize(cartItemsBefore.size());
            assertThat(result.getOrderItems())
                .allSatisfy(oi -> {
                    CartItem ci = cartItemsBefore.stream()
                                                 .filter(c -> c.getProduct().getName().equals(oi.getProductName())
                                                     && c.getProduct().getBrandName().equals(oi.getProductBrandName())
                                                 ).findFirst().orElseThrow();
                    
                    assertThat(oi.getRequestedQty()).isEqualTo(ci.getRequestedQty());
                    assertThat(oi.getProductPrice()).isEqualByComparingTo(ci.getProductPrice());
                });
            
            // Assert: order is linked to the user profile and is the only order
            assertThat(userProfile.getOrders()).containsExactly(result);
            assertThat(result.getUserProfile()).isSameAs(userProfile);
            
            // Assert: cart removed from profile after successful order creation
            assertThat(userProfile.getCart()).isNull();
        }
        
        @Test
        public void createOrder_should_ThrowEntityNotFound_when_UserHasNoCart() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            UserProfile userProfile = fixtures.userProfileEntity();
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            when(cartService.findByUserIdWithItems(fixtures.userId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            // Assert: throws when user has no cart
            assertThrows(EntityNotFoundException.class, () -> underTest.selfCreateOrder());
            
            // Assert: services were called to load profile & cart
            verify(userProfileService).findByUserId(fixtures.userId());
            verify(cartService).findByUserIdWithItems(fixtures.userId());
            
            // Assert: no stock ops / no persistence when cart missing
            verifyNoInteractions(productService, orderRepository);
        }
        
        @Test
        public void createOrder_should_ThrowCartEmpty_when_CartEmpty() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            Cart cart = fixtures.cartEntity();
            UserProfile userProfile = fixtures.userProfileEntity();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            when(cartService.findByUserIdWithItems(fixtures.userId())).thenReturn(cart);
            
            // When & Then
            // Assert: throws when user has empty cart
            assertThrows(CartEmptyException.class, () -> underTest.selfCreateOrder());
            
            // Assert: services were called to load profile & cart
            verify(userProfileService).findByUserId(fixtures.userId());
            verify(cartService).findByUserIdWithItems(fixtures.userId());
            
            // Assert: no stock ops / no persistence when cart empty
            verifyNoInteractions(productService, orderRepository);
            
            
        }
        
        @Test
        public void createOrder_should_ThrowInsufficientStock_when_StockChanged() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            UserProfile userProfile = fixtures.userProfileEntity();
            Cart cart = fixtures.cartEntity();
            CartItem cartItem = fixtures.cartItemEntity();
            
            user.setProfileAndLink(userProfile);
            userProfile.setCartAndLink(cart);
            cart.addCartItemAndLink(cartItem);
            
            Product product = cartItem.getProduct();
            product.setAvailableQty(0);
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            when(cartService.findByUserIdWithItems(fixtures.userId())).thenReturn(cart);
            
            // When & Then
            assertThrows(InsufficientStockException.class, () -> underTest.selfCreateOrder());
            
            // Assert: services loaded the profile and cart
            verify(userProfileService).findByUserId(fixtures.userId());
            verify(cartService).findByUserIdWithItems(fixtures.userId());
            
            // Assert: no persistence / linking happened after failure
            assertThat(userProfile.getOrders()).isEmpty();
            assertThat(userProfile.getCart()).isSameAs(cart);
            verifyNoInteractions(orderRepository);
        }
    }
}
