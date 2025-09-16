package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.entities.Cart;
import com.sobow.shopping.domain.entities.CartItem;
import com.sobow.shopping.domain.entities.Product;
import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;
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
    
    @InjectMocks
    private CartServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("createCartItem")
    class createCartItem {
        
        @Test
        public void createCartItem_should_CreateNewCartItem_and_AddToCart_when_CartItemDoesNotExist() {
            // Given
            Cart cart = fixtures.cart();
            Product product = fixtures.productEntity();
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
            when(productService.findById(product.getId())).thenReturn(product);
            when(cartItemRepository.existsByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(false);
            
            // When
            CartItem result = underTest.createCartItem(cart.getId(), request);
            
            // Then
            assertThat(result.getProduct()).isSameAs(product);
            assertThat(result.getQuantity()).isEqualTo(request.requestedQty());
            assertThat(result.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).hasSize(1).contains(result);
        }
        
        @Test
        public void createCartItem_should_ThrowNotFound_when_CartDoesNotExist() {
            Cart cart = fixtures.cart();
            Product product = fixtures.productEntity();
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(cartRepository.findById(cart.getId())).thenThrow(new EntityNotFoundException());
            
            assertThrows(EntityNotFoundException.class, () -> underTest.createCartItem(cart.getId(), request));
            assertThat(cart.getCartItems()).isEmpty();
        }
        
        @Test
        public void createCartItem_should_ThrowNotFound_when_ProductDoesNotExist() {
            Cart cart = fixtures.cart();
            CartItemCreateRequest request = fixtures.withProductId(fixtures.nonExistingId())
                                                    .cartItemCreateRequest();
            
            when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
            when(productService.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            assertThrows(EntityNotFoundException.class, () -> underTest.createCartItem(cart.getId(), request));
            assertThat(cart.getCartItems()).isEmpty();
        }
        
        @Test
        public void createCartItem_should_ThrowAlreadyExists_when_CartItemAlreadyExists() {
            Cart cart = fixtures.cart();
            var itemsBefore = List.copyOf(cart.getCartItems());
            Product product = fixtures.productEntity();
            CartItemCreateRequest request = fixtures.cartItemCreateRequest();
            
            when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
            when(productService.findById(product.getId())).thenReturn(product);
            when(cartItemRepository.existsByCartIdAndProductId(cart.getId(), product.getId()))
                .thenThrow(new CartItemAlreadyExistsException(cart.getId(), product.getId()));
            
            assertThrows(CartItemAlreadyExistsException.class, () -> underTest.createCartItem(cart.getId(), request));
            assertThat(cart.getCartItems()).hasSize(itemsBefore.size());
        }
    }
    
    @Nested
    @DisplayName("updateCartItemQty")
    class updateCartItemQty {
        
        @Test
        public void updateCartItemQty_should_UpdateItemQty_when_NewQtyWithinZero_and_AvailableStock() {
            // Given
            Cart cart = fixtures.cart();
            CartItem item = fixtures.cartItem();
            cart.addCartItemAndLink(item);
            
            CartItemUpdateRequest request = fixtures.withRequestedQty(2)
                                                    .cartItemUpdateRequest();
            
            when(cartItemRepository.findByCartIdAndId(cart.getId(), item.getId())).thenReturn(Optional.of(item));
            
            // When
            CartItem result = underTest.updateCartItemQty(cart.getId(), item.getId(), request);
            
            // Then
            assertThat(result).isSameAs(item);
            assertThat(result.getQuantity()).isEqualTo(request.requestedQty());
            assertThat(cart.getCartItems()).contains(item);
        }
        
        @Test
        public void updateCartItemQty_should_RemoveItemFromCart_when_NewQtyEqualsZero() {
            // given
            Cart cart = fixtures.cart();
            CartItem item = fixtures.cartItem();
            cart.addCartItemAndLink(item);
            
            CartItemUpdateRequest request = fixtures.withRequestedQty(0)
                                                    .cartItemUpdateRequest();
            
            when(cartItemRepository.findByCartIdAndId(cart.getId(), item.getId())).thenReturn(Optional.of(item));
            
            // when
            CartItem result = underTest.updateCartItemQty(cart.getId(), item.getId(), request);
            
            // then
            assertThat(result).isSameAs(item);
            assertThat(result.getQuantity()).isZero();
            
            assertThat(cart.getCartItems()).doesNotContain(result);
            assertThat(result.getCart()).isNull();
        }
        
        @Test
        public void updateCartItemQty_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            Cart cart = fixtures.cart();
            
            CartItemUpdateRequest request = fixtures.cartItemUpdateRequest();
            
            when(cartItemRepository.findByCartIdAndId(cart.getId(), fixtures.nonExistingId()))
                .thenThrow(new EntityNotFoundException());
            
            assertThrows(EntityNotFoundException.class,
                         () -> underTest.updateCartItemQty(cart.getId(), fixtures.nonExistingId(), request));
        }
    }
    
    @Nested
    @DisplayName("removeCartItem")
    class removeCartItem {
        
        @Test
        public void removeCartItem_should_RemoveItem_when_ItemExistsInCart() {
            // Given
            Cart cart = fixtures.cart();
            CartItem item = fixtures.cartItem();
            cart.addCartItemAndLink(item);
            
            // Precondition: they were related
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(cartItemRepository.findByCartIdAndId(cart.getId(), item.getId())).thenReturn(Optional.of(item));
            
            // When
            underTest.removeCartItem(cart.getId(), item.getId());
            
            // Then
            assertThat(cart.getCartItems()).doesNotContain(item);
            assertThat(item.getCart()).isNull();
        }
        
        @Test
        public void removeCartItem_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            // Given
            Cart cart = fixtures.cart();
            CartItem item = fixtures.cartItem();
            
            assertThat(cart.getCartItems()).doesNotContain(item);
            assertThat(item.getCart()).isNull();
            
            when(cartItemRepository.findByCartIdAndId(cart.getId(), item.getId()))
                .thenThrow(new EntityNotFoundException());
            
            // When
            assertThrows(EntityNotFoundException.class, () -> underTest.removeCartItem(cart.getId(), item.getId()));
        }
        
        @Test
        public void removeCartItem_should_BeIdempotent_when_CalledTwice_SecondCallThrowsNotFound() {
            // Given
            Cart cart = fixtures.cart();
            CartItem item = fixtures.cartItem();
            cart.addCartItemAndLink(item);
            
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(cartItemRepository.findByCartIdAndId(cart.getId(), item.getId()))
                .thenReturn(Optional.of(item))
                .thenThrow(new EntityNotFoundException());
            
            underTest.removeCartItem(cart.getId(), item.getId());
            
            assertThat(cart.getCartItems()).doesNotContain(item);
            assertThat(item.getCart()).isNull();
            
            // When & Then
            assertThrows(EntityNotFoundException.class, () -> underTest.removeCartItem(cart.getId(), item.getId()));
        }
    }
    
    @Nested
    @DisplayName("removeAllCartItems")
    class removeAllCartItems {
        
        @Test
        public void removeAllCartItems_should_RemoveAllCartItems_when_CartExists() {
            // Given
            Cart cart = fixtures.cart();
            CartItem item = fixtures.cartItem();
            cart.addCartItemAndLink(item);
            
            assertThat(item.getCart()).isSameAs(cart);
            assertThat(cart.getCartItems()).contains(item);
            
            when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
            
            // When
            underTest.removeAllCartItems(cart.getId());
            
            // Then
            assertThat(cart.getCartItems()).doesNotContain(item);
            assertThat(item.getCart()).isNull();
        }
        
        @Test
        public void removeAllCartItems_should_ThrowNotFound_when_CartDoesNotExist() {
            when(cartRepository.findById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            assertThrows(EntityNotFoundException.class, () -> underTest.removeAllCartItems(fixtures.nonExistingId()));
        }
    }
}
