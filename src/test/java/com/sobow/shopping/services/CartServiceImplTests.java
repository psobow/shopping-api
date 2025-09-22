package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.dto.CartItemUpdateRequest;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.exceptions.CartItemAlreadyExistsException;
import com.sobow.shopping.repositories.CartItemRepository;
import com.sobow.shopping.repositories.CartRepository;
import com.sobow.shopping.services.Impl.CartServiceImpl;
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

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTests {
    
    @Mock
    private CartRepository cartRepository;
    
    @Mock
    private CartItemRepository cartItemRepository;
    
    @Mock
    private ProductService productService;
    
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
            UserProfile userProfile = fixtures.userProfileEntity();
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            assertThat(userProfile.getCart()).isNull();
            
            // When
            Cart result = underTest.createOrGetCart(fixtures.userId());
            
            // Then
            assertThat(result).isNotNull();
            assertThat(userProfile.getCart()).isSameAs(result);
        }
        
        @Test
        public void createOrGetCart_should_ReturnExistingCart_when_CartExists() {
            // Given
            UserProfile userProfile = fixtures.userProfileEntity();
            Cart cart = fixtures.cartEntity();
            userProfile.setCartAndLink(cart);
            assertThat(userProfile.getCart()).isNotNull();
            
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            
            // When
            Cart result = underTest.createOrGetCart(fixtures.userId());
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result).isSameAs(cart);
        }
    }
    
    @Nested
    @DisplayName("removeCart")
    class removeCart {
        
        @Test
        public void removeCart_should_removeCart_when_CartExist() {
            // Given
            UserProfile userProfile = fixtures.userProfileEntity();
            Cart cart = fixtures.cartEntity();
            userProfile.setCartAndLink(cart);
            assertThat(userProfile.getCart()).isNotNull();
            
            when(userProfileService.findByUserId(fixtures.userId())).thenReturn(userProfile);
            
            // When
            underTest.removeCart(fixtures.userId());
            
            // Then
            assertThat(userProfile.getCart()).isNull();
        }
        
        @Test
        public void removeCart_should_ThrowEntityNotFound_when_UserDoesNotExist() {
            // Given
            when(userProfileService.findByUserId(fixtures.userId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.removeCart(fixtures.userId()));
        }
    }
    
    @Nested
    @DisplayName("createCartItem")
    class createCartItem {
        
        @Test
        public void createCartItem_should_CreateNewCartItem_and_AddToCart_when_CartItemDoesNotExist() {
            // Given
            Cart cart = fixtures.cartEntity();
            Product product = fixtures.productEntity();
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(cartRepository.findById(fixtures.cartId())).thenReturn(Optional.of(cart));
            when(productService.findById(fixtures.productId())).thenReturn(product);
            when(cartItemRepository.existsByCartIdAndProductId(fixtures.cartId(), fixtures.productId())).thenReturn(false);
            
            // When
            CartItem result = underTest.createCartItem(fixtures.cartId(), request);
            
            // Then
            assertThat(result.getProduct()).isSameAs(product);
            assertThat(result.getRequestedQty()).isEqualTo(request.requestedQty());
            
            assertThat(result.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).hasSize(1).contains(result);
        }
        
        @Test
        public void createCartItem_should_ThrowNotFound_when_CartDoesNotExist() {
            // Given
            Cart cart = fixtures.cartEntity();
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(cartRepository.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.createCartItem(fixtures.nonExistingId(), request));
            assertThat(cart.getCartItems()).isEmpty();
        }
        
        @Test
        public void createCartItem_should_ThrowNotFound_when_ProductDoesNotExist() {
            // Given
            Cart cart = fixtures.cartEntity();
            CartItemCreateRequest request = fixtures.withProductId(fixtures.nonExistingId())
                                                    .cartItemCreateRequest();
            
            when(cartRepository.findById(fixtures.cartId())).thenReturn(Optional.of(cart));
            when(productService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.createCartItem(fixtures.cartId(), request));
            assertThat(cart.getCartItems()).isEmpty();
        }
        
        @Test
        public void createCartItem_should_ThrowAlreadyExists_when_CartItemAlreadyExists() {
            // Given
            Cart cart = fixtures.cartEntity();
            var itemsBefore = List.copyOf(cart.getCartItems());
            
            Product product = fixtures.productEntity();
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(cartRepository.findById(fixtures.cartId())).thenReturn(Optional.of(cart));
            when(productService.findById(fixtures.productId())).thenReturn(product);
            when(cartItemRepository.existsByCartIdAndProductId(fixtures.cartId(), fixtures.productId()))
                .thenThrow(new CartItemAlreadyExistsException(fixtures.cartId(), fixtures.productId()));
            
            // When & Then
            assertThrows(CartItemAlreadyExistsException.class, () -> underTest.createCartItem(fixtures.cartId(), request));
            assertThat(cart.getCartItems()).hasSize(itemsBefore.size());
        }
    }
    
    @Nested
    @DisplayName("updateCartItemQty")
    class updateCartItemQty {
        
        @Test
        public void updateCartItemQty_should_UpdateItemQty_when_NewQtyWithinZero_and_AvailableStock() {
            // Given
            Cart cart = fixtures.cartEntity();
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            CartItemUpdateRequest request = fixtures.withRequestedQty(2)
                                                    .cartItemUpdateRequest();
            
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId())).thenReturn(Optional.of(item));
            
            // When
            CartItem result = underTest.updateCartItemQty(fixtures.cartId(), fixtures.cartItemId(), request);
            
            // Then
            assertThat(result).isSameAs(item);
            assertThat(result.getRequestedQty()).isEqualTo(request.requestedQty());
            assertThat(cart.getCartItems()).contains(item);
        }
        
        @Test
        public void updateCartItemQty_should_RemoveItemFromCart_when_NewQtyEqualsZero() {
            // Given
            Cart cart = fixtures.cartEntity();
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            CartItemUpdateRequest request = fixtures.withRequestedQty(0)
                                                    .cartItemUpdateRequest();
            
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId())).thenReturn(Optional.of(item));
            
            // When
            CartItem result = underTest.updateCartItemQty(fixtures.cartId(), fixtures.cartItemId(), request);
            
            // Rhen
            assertThat(result).isSameAs(item);
            assertThat(result.getRequestedQty()).isZero();
            
            assertThat(cart.getCartItems()).doesNotContain(result);
        }
        
        @Test
        public void updateCartItemQty_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            // Given
            CartItemUpdateRequest request = fixtures.cartItemUpdateRequest();
            
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.nonExistingId()))
                .thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.updateCartItemQty(fixtures.cartId(), fixtures.nonExistingId(), request));
        }
    }
    
    @Nested
    @DisplayName("removeCartItem")
    class removeCartItem {
        
        @Test
        public void removeCartItem_should_RemoveItem_when_ItemExistsInCart() {
            // Given
            Cart cart = fixtures.cartEntity();
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            // Precondition: they were related
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId())).thenReturn(Optional.of(item));
            
            // When
            underTest.removeCartItem(fixtures.cartId(), fixtures.cartItemId());
            
            // Then
            assertThat(cart.getCartItems()).doesNotContain(item);
        }
        
        @Test
        public void removeCartItem_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            // Given
            Cart cart = fixtures.cartEntity();
            CartItem item = fixtures.cartItemEntity();
            
            assertThat(cart.getCartItems()).doesNotContain(item);
            assertThat(item.getCart()).isNull();
            
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId()))
                .thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.removeCartItem(fixtures.cartId(), fixtures.cartItemId()));
        }
        
        @Test
        public void removeCartItem_should_BeIdempotent_when_CalledTwice_SecondCallThrowsNotFound() {
            // Given
            Cart cart = fixtures.cartEntity();
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(cartItemRepository.findByCartIdAndId(fixtures.cartId(), fixtures.cartItemId()))
                .thenReturn(Optional.of(item))
                .thenThrow(new EntityNotFoundException());
            
            underTest.removeCartItem(fixtures.cartId(), fixtures.cartItemId());
            
            assertThat(cart.getCartItems()).doesNotContain(item);
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.removeCartItem(fixtures.cartId(), fixtures.cartItemId()));
        }
    }
    
    @Nested
    @DisplayName("removeAllCartItems")
    class removeAllCartItems {
        
        @Test
        public void removeAllCartItems_should_RemoveAllCartItems_when_CartExists() {
            // Given
            Cart cart = fixtures.cartEntity();
            CartItem item = fixtures.cartItemEntity();
            cart.addCartItemAndLink(item);
            
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(cartRepository.findById(fixtures.cartId())).thenReturn(Optional.of(cart));
            
            // When
            underTest.removeAllCartItems(fixtures.cartId());
            
            // Then
            assertThat(cart.getCartItems()).doesNotContain(item);
        }
        
        @Test
        public void removeAllCartItems_should_ThrowNotFound_when_CartDoesNotExist() {
            // Given
            when(cartRepository.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.removeAllCartItems(fixtures.nonExistingId()));
        }
    }
}
