package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.dto.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.dto.CartItemUpdateRequest;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.exceptions.CartItemAlreadyExistsException;
import com.sobow.shopping.repositories.CartItemRepository;
import com.sobow.shopping.repositories.CartRepository;
import com.sobow.shopping.services.CartService;
import com.sobow.shopping.services.CurrentUserService;
import com.sobow.shopping.services.ProductService;
import com.sobow.shopping.services.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CurrentUserService currentUserService;
    private final UserProfileService userProfileService;
    
    @Override
    public Cart findByUserIdWithItems(long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                             .orElseThrow(() -> new EntityNotFoundException("Cart of user " + userId + " not found"));
    }
    
    @Transactional
    @Override
    public Cart selfCreateOrGetCart() {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        
        UserProfile userProfile = userProfileService.findByUserId(user.getId());
        if (userProfile.getCart() != null) return findByUserIdWithItems(user.getId());
        
        Cart newCart = new Cart();
        userProfile.setCartAndLink(newCart);
        return newCart;
    }
    
    @Transactional
    @Override
    public void selfRemoveCart() {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        UserProfile userProfile = userProfileService.findByUserId(user.getId());
        userProfile.removeCart();
    }
    
    @Transactional
    @Override
    public CartItem selfCreateCartItem(CartItemCreateRequest createRequest) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        
        Cart cart = findByUserIdWithItems(user.getId());
        
        Long productId = createRequest.productId();
        Product product = productService.findById(productId);
        
        // I could update existing CartItem when already in the Cart, instead of throwing exception
        boolean itemExistsInCart = cartItemRepository.existsByCartIdAndProductId(cart.getId(), productId);
        if (itemExistsInCart) throw new CartItemAlreadyExistsException(cart.getId(), productId);
        
        CartItem newItem = new CartItem(product, createRequest.requestedQty());
        cart.addCartItemAndLink(newItem);
        return newItem;
    }
    
    @Transactional
    @Override
    public CartItem selfUpdateCartItemQty(long itemId, CartItemUpdateRequest updateRequest) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        Cart cart = findByUserIdWithItems(user.getId());
        
        CartItem item = findCartItemByCartIdAndItemId(cart.getId(), itemId);
        item.updateFrom(updateRequest);
        if (item.isEmpty()) selfRemoveCartItem(itemId);
        return item;
    }
    
    @Transactional
    @Override
    public void selfRemoveCartItem(long itemId) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        Cart cart = findByUserIdWithItems(user.getId());
        
        CartItem item = findCartItemByCartIdAndItemId(cart.getId(), itemId);
        cart.removeCartItem(item);
    }
    
    @Transactional
    @Override
    public void selfRemoveAllCartItems() {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        Cart cart = findByUserIdWithItems(user.getId());
        cart.removeAllCartItems();
    }
    
    @Override
    public boolean exists() {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        
        return cartRepository.existsByUserProfile_User_Id(user.getId());
    }
    
    private CartItem findCartItemByCartIdAndItemId(long cartId, long itemId) {
        return cartItemRepository.findByCartIdAndId(cartId, itemId)
                                 .orElseThrow(() -> new EntityNotFoundException(
                                     "CartItem with id " + itemId + " not found in cart with id " + cartId));
    }
}

