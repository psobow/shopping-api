package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.entities.Cart;
import com.sobow.shopping.domain.entities.CartItem;
import com.sobow.shopping.domain.entities.Product;
import com.sobow.shopping.domain.entities.UserProfile;
import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;
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
        userProfile.addCartAndLink(newCart);
        return newCart;
    }
    
    @Transactional
    @Override
    public void removeCart(long userId) {
        UserProfile userProfile = userProfileService.findByUserId(userId);
        userProfile.removeCartAndUnlink();
    }
    
    @Override
    public Cart findById(long id) {
        return cartRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Cart with " + id + " not found"));
    }
    
    @Transactional
    @Override
    public CartItem createCartItem(long cartId, CartItemCreateRequest request) {
        Cart cart = findById(cartId);
        Product product = productService.findById(request.productId());
        
        // I could update existing CartItem when already in the Cart
        boolean itemExistsInCart = cartItemRepository.existsByCartIdAndProductId(cartId, product.getId());
        if (itemExistsInCart) throw new CartItemAlreadyExistsException(cartId, product.getId());
        
        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(request.requestedQty());
        cart.addCartItemAndLink(newItem);
        return newItem;
    }
    
    @Transactional
    @Override
    public CartItem updateCartItemQty(long cartId, long itemId, CartItemUpdateRequest request) {
        CartItem item = findCartItemByCartIdAndId(cartId, itemId);
        int resultQuantity = item.setQuantity(request.requestedQty());
        if (resultQuantity == 0) removeCartItem(cartId, itemId);
        return item;
    }
    
    @Transactional
    @Override
    public void removeCartItem(long cartId, long itemId) {
        CartItem item = findCartItemByCartIdAndId(cartId, itemId);
        Cart cart = item.getCart();
        cart.removeCartItemAndUnlink(item);
    }
    
    @Transactional
    @Override
    public void removeAllCartItems(long cartId) {
        Cart cart = findById(cartId);
        cart.removeAllCartItems();
    }
    
    @Override
    public boolean existsByUserProfile_UserId(long userId) {
        return cartRepository.existsByUserProfile_UserId(userId);
    }
    
    private CartItem findCartItemByCartIdAndId(long cartId, long itemId) {
        return cartItemRepository.findByCartIdAndId(cartId, itemId)
                                 .orElseThrow(() -> new EntityNotFoundException(
                                     "CartItem with id " + itemId + " not found in cart with id" + cartId));
    }
}

