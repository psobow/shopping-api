package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.Product;
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
            CartItemUpdateRequest request = fixtures.cartItemUpdateRequest();
            
            when(cartItemRepository.findByCartIdAndId(cart.getId(), request.cartItemId())).thenReturn(Optional.of(item));
            
            // When
            CartItem result = underTest.updateCartItemQty(cart.getId(), request);
            
            // Then
            assertThat(result).isSameAs(item);
            assertThat(result.getQuantity()).isEqualTo(request.requestedQty());
            assertThat(cart.getCartItems()).contains(item);
        }
        
        @Test
        public void updateCartItemQty_should_RemoveItemFromCart_when_NewQtyEqualsZero() {
            fail("Implement me");
        }
        
        @Test
        public void updateCartItemQty_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            fail("Implement me");
        }
    }
    
    @Nested
    @DisplayName("removeCartItem")
    class removeCartItem {
        
        @Test
        public void removeCartItem_should_RemoveItem_when_ItemExistsInCart() {
            fail("Implement me");
        }
        
        @Test
        public void removeCartItem_should_ThrowNotFound_when_ItemDoesNotExistInCart() {
            fail("Implement me");
        }
        
        @Test
        public void removeCartItem_should_BeIdempotent_when_CalledTwice_SecondCallThrowsNotFound() {
            fail("Implement me");
        }
    }
    
    @Nested
    @DisplayName("removeAllCartItems")
    class removeAllCartItems {
        
        @Test
        public void removeAllCartItems_should_RemoveAllCartItems_when_CartExists() {
            fail("Implement me");
        }
        
        @Test
        public void removeAllCartItems_should_ThrowNotFound_when_CartDoesNotExist() {
            fail("Implement me");
        }
    }
}
