package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.dto.CartItemUpdateRequest;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.exceptions.CartItemAlreadyExistsException;
import com.sobow.shopping.repositories.CartItemRepository;
import com.sobow.shopping.repositories.CartRepository;
import com.sobow.shopping.services.CartService;
import com.sobow.shopping.services.ProductService;
import com.sobow.shopping.services.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserProfileService userProfileService;
    
    @Transactional
    @Override
    public Cart createOrGetCart(long userId) {
        UserProfile userProfile = userProfileService.findByUserId(userId);
        if (userProfile.getCart() != null) return userProfile.getCart();
        
        Cart newCart = new Cart();
        userProfile.setCartAndLink(newCart);
        return newCart;
    }
    
    @Transactional
    @Override
    public void removeCart(long userId) {
        UserProfile userProfile = userProfileService.findByUserId(userId);
        userProfile.removeCart();
    }
    
    @Override
    public Cart findById(long id) {
        return cartRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Cart with " + id + " not found"));
    }
    
    @Override
    public Cart findByUserIdWithItems(long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                             .orElseThrow(() -> new EntityNotFoundException("Cart of user " + userId + " not found"));
    }
    
    @Transactional
    @Override
    public CartItem createCartItem(long cartId, CartItemCreateRequest createRequest) {
        Cart cart = findById(cartId);
        Long productId = createRequest.productId();
        Product product = productService.findById(productId);
        
        // I could update existing CartItem when already in the Cart, instead of throwing exception
        boolean itemExistsInCart = cartItemRepository.existsByCartIdAndProductId(cartId, productId);
        if (itemExistsInCart) throw new CartItemAlreadyExistsException(cartId, productId);
        
        CartItem newItem = new CartItem(product, createRequest.requestedQty());
        cart.addCartItemAndLink(newItem);
        return newItem;
    }
    
    @Transactional
    @Override
    public CartItem updateCartItemQty(long cartId, long itemId, CartItemUpdateRequest updateRequest) {
        CartItem item = findCartItemByCartIdAndId(cartId, itemId);
        item.updateFrom(updateRequest);
        if (item.isEmpty()) removeCartItem(cartId, itemId);
        return item;
    }
    
    @Transactional
    @Override
    public void removeCartItem(long cartId, long itemId) {
        CartItem item = findCartItemByCartIdAndId(cartId, itemId);
        Cart cart = item.getCart();
        cart.removeCartItem(item);
    }
    
    @Transactional
    @Override
    public void removeAllCartItems(long cartId) {
        Cart cart = findById(cartId);
        cart.removeAllCartItems();
    }
    
    @Override
    public boolean existsByUserProfile_UserId(long userId) {
        return cartRepository.existsByUserProfile_User_Id(userId);
    }
    
    private CartItem findCartItemByCartIdAndId(long cartId, long itemId) {
        return cartItemRepository.findByCartIdAndId(cartId, itemId)
                                 .orElseThrow(() -> new EntityNotFoundException(
                                     "CartItem with id " + itemId + " not found in cart with id" + cartId));
    }
}

