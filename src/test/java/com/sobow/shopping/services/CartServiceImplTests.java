package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.controllers.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.controllers.cart.dto.CartItemUpdateRequest;
import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.CartItemRepository;
import com.sobow.shopping.domain.cart.CartRepository;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.exceptions.CartItemAlreadyExistsException;
import com.sobow.shopping.services.cart.Impl.CartServiceImpl;
import com.sobow.shopping.services.product.ProductService;
import com.sobow.shopping.services.user.CurrentUserService;
import com.sobow.shopping.services.user.UserProfileService;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTests {
    
    @Mock
    private CartRepository cartRepository;
    
    @Mock
    private CartItemRepository cartItemRepository;
    
    @Mock
    private ProductService productService;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private UserProfileService userProfileService;
    
    @InjectMocks
    private CartServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("createOrGetCart")
    class createOrGetCart {
        
        @Test
        public void createOrGetCart_should_createNewCart_when_CartDoesNotExist() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            UserProfile userProfile = fixtures.userProfileEntity();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            assertThat(userProfile.getCart()).isNull();
            
            // When
            Cart result = underTest.selfCreateOrGetCart();
            
            // Then
            // Assert: user profile was loaded
            verify(userProfileService).findByUserId(fixtures.userId());
            
            // Assert: a new cart instance was created and returned
            assertThat(result).isNotNull();
            
            // Assert: new cart is linked back to the user profile
            assertThat(userProfile.getCart()).isSameAs(result);
            assertThat(result.getUserProfile()).isSameAs(userProfile);
            
            // Assert: new cart has no items
            assertThat(result.getCartItems()).isEmpty();
        }
        
        @Test
        public void createOrGetCart_should_ReturnExistingCart_when_CartExists() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            UserProfile userProfile = fixtures.userProfileEntity();
            Cart cart = fixtures.cartEntity();
            userProfile.setCartAndLink(cart);
            
            assertThat(userProfile.getCart()).isNotNull();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            
            // When
            Cart result = underTest.selfCreateOrGetCart();
            
            // Then
            // Assert: profile was looked up once
            verify(userProfileService).findByUserId(fixtures.userId());
            
            // Assert: returns the same existing cart instance
            assertThat(result).isNotNull();
            assertThat(result).isSameAs(cart);
            
            // Assert: userProfile still points to that cart (no relinking/replacement)
            assertThat(userProfile.getCart()).isSameAs(cart);
        }
    }
    
    @Nested
    @DisplayName("removeCart")
    class removeCart {
        
        @Test
        public void removeCart_should_removeCart_when_CartExist() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            UserProfile userProfile = fixtures.userProfileEntity();
            Cart cart = fixtures.cartEntity();
            userProfile.setCartAndLink(cart);
            
            assertThat(userProfile.getCart()).isNotNull();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            
            // When
            underTest.selfRemoveCart();
            
            // Then
            // Assert: cart was removed
            assertThat(userProfile.getCart()).isNull();
        }
        
        @Test
        public void removeCart_should_ThrowEntityNotFound_when_UserDoesNotExist() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(userProfileService.findByUserId(fixtures.userId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            // Assert: throw when user does not exist
            assertThrows(EntityNotFoundException.class, () -> underTest.selfRemoveCart());
        }
    }
    
    @Nested
    @DisplayName("createCartItem")
    class createCartItem {
        
        @Test
        public void createCartItem_should_CreateNewCartItem_and_AddToCart_when_CartItemDoesNotExist() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            Product product = fixtures.productEntity();
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(productService.findById(fixtures.productId())).thenReturn(product);
            when(cartItemRepository.existsByCartIdAndProductId(fixtures.cartId(), fixtures.productId())).thenReturn(false);
            
            // When
            CartItem result = underTest.selfCreateCartItem(request);
            
            // Then
            // Assert: cart was loaded
            verify(cartRepository).findByUserIdWithItems(fixtures.userId());
            
            // Assert: product was loaded
            verify(productService).findById(fixtures.productId());
            
            // Assert: uniqueness check performed for (cartId, productId)
            verify(cartItemRepository).existsByCartIdAndProductId(fixtures.cartId(), fixtures.productId());
            
            // Assert: new CartItem was created with correct links & data
            assertThat(result.getProduct()).isSameAs(product);
            assertThat(result.getRequestedQty()).isEqualTo(request.requestedQty());
            assertThat(result.getCart()).isSameAs(cart);
            
            // Assert: cart now contains exactly this new item
            assertThat(cart.getCartItems()).hasSize(1).contains(result);
        }
        
        @Test
        public void createCartItem_should_ThrowNotFound_when_CartDoesNotExist() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            // Assert: throw when cart does not exist
            assertThrows(EntityNotFoundException.class, () -> underTest.selfCreateCartItem(request));
            
            // Assert: no downstream calls when cart not found
            verifyNoInteractions(productService, cartItemRepository);
        }
        
        @Test
        public void createCartItem_should_ThrowNotFound_when_ProductDoesNotExist() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            CartItemCreateRequest request = fixtures.withProductId(fixtures.nonExistingId())
                                                    .cartItemCreateRequest();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(productService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            // Assert: throw when product does not exist
            assertThrows(EntityNotFoundException.class, () -> underTest.selfCreateCartItem(request));
            
            // Assert: cart not modified
            assertThat(cart.getCartItems()).isEmpty();
        }
        
        @Test
        public void createCartItem_should_ThrowAlreadyExists_when_CartItemAlreadyExists() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            var itemsBefore = List.copyOf(cart.getCartItems());
            
            Product product = fixtures.productEntity();
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(productService.findById(fixtures.productId())).thenReturn(product);
            when(cartItemRepository.existsByCartIdAndProductId(fixtures.cartId(), fixtures.productId()))
                .thenThrow(new CartItemAlreadyExistsException(fixtures.cartId(), fixtures.productId()));
            
            // When & Then
            assertThrows(CartItemAlreadyExistsException.class, () -> underTest.selfCreateCartItem(request));
            // Assert: cart not modified
            assertThat(cart.getCartItems()).hasSize(itemsBefore.size());
        }
    }
    
    @Nested
    @DisplayName("updateCartItemQty")
    class updateCartItemQty {
        
        @Test
        public void updateCartItemQty_should_UpdateItemQty_when_NewQtyWithinZero_and_AvailableStock() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            CartItemUpdateRequest request = fixtures.withRequestedQty(2)
                                                    .cartItemUpdateRequest();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId())).thenReturn(Optional.of(item));
            
            // When
            CartItem result = underTest.selfUpdateCartItemQty(fixtures.cartItemId(), request);
            
            // Then
            // Assert: item was looked up by (cartId, itemId)
            verify(cartItemRepository).findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId());
            
            // Assert: the same entity instance is returned
            assertThat(result).isSameAs(item);
            
            // Assert: quantity was updated from the request
            assertThat(result.getRequestedQty()).isEqualTo(request.requestedQty());
            
            // Assert: item remains linked to the cart
            assertThat(cart.getCartItems()).contains(item);
        }
        
        @Test
        public void updateCartItemQty_should_RemoveItemFromCart_when_NewQtyEqualsZero() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            CartItemUpdateRequest request = fixtures.withRequestedQty(0)
                                                    .cartItemUpdateRequest();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId())).thenReturn(Optional.of(item));
            
            // When
            CartItem result = underTest.selfUpdateCartItemQty(fixtures.cartItemId(), request);
            
            // Then
            // Assert: item was looked up by (cartId, itemId) and looked up again for removal
            verify(cartItemRepository, times(2)).findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId());
            
            // Assert: same instance is mutated and returned
            assertThat(result).isSameAs(item);
            
            // Assert: qty updated to zero and item considered empty
            assertThat(result.getRequestedQty()).isZero();
            assertThat(result.isEmpty()).isTrue();
            
            // Assert: item was removed from the cart collection
            assertThat(cart.getCartItems()).doesNotContain(result);
        }
        
        @Test
        public void updateCartItemQty_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            
            CartItemUpdateRequest request = fixtures.cartItemUpdateRequest();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.nonExistingId()))
                .thenThrow(new EntityNotFoundException());
            
            // When & Then
            // Assert: throw when item does not exist
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.selfUpdateCartItemQty(fixtures.nonExistingId(), request));
        }
    }
    
    @Nested
    @DisplayName("removeCartItem")
    class removeCartItem {
        
        @Test
        public void removeCartItem_should_RemoveItem_when_ItemExistsInCart() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            // Precondition: they were related
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId())).thenReturn(Optional.of(item));
            
            // When
            underTest.selfRemoveCartItem(fixtures.cartItemId());
            
            // Then
            // Assert: item was looked up via (cartId, itemId)
            verify(cartItemRepository).findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId());
            
            // Assert: item no longer present in the cart
            assertThat(cart.getCartItems()).doesNotContain(item);
        }
        
        @Test
        public void removeCartItem_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            CartItem item = fixtures.cartItemEntity();
            
            assertThat(cart.getCartItems()).doesNotContain(item);
            assertThat(item.getCart()).isNull();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId()))
                .thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.selfRemoveCartItem(fixtures.cartItemId()));
        }
        
        @Test
        public void removeCartItem_should_BeIdempotent_when_CalledTwice_SecondCallThrowsNotFound() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            Cart cart = fixtures.cartEntity();
            ReflectionTestUtils.setField(cart, "id", fixtures.cartId());
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId()))
                .thenReturn(Optional.of(item))
                .thenThrow(new EntityNotFoundException());
            
            underTest.selfRemoveCartItem(fixtures.cartItemId());
            
            assertThat(cart.getCartItems()).doesNotContain(item);
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.selfRemoveCartItem(fixtures.cartItemId()));
        }
    }
    
    @Nested
    @DisplayName("removeAllCartItems")
    class removeAllCartItems {
        
        @Test
        public void removeAllCartItems_should_RemoveAllCartItems_when_CartExists() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            Cart cart = fixtures.cartEntity();
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenReturn(Optional.of(cart));
            
            // When
            underTest.selfRemoveAllCartItems();
            
            // Then
            // Assert: item no longer present in the cart
            assertThat(cart.getCartItems()).doesNotContain(item);
        }
        
        @Test
        public void removeAllCartItems_should_ThrowNotFound_when_CartDoesNotExist() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(cartRepository.findByUserIdWithItems(fixtures.userId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.selfRemoveAllCartItems());
        }
    }
}
