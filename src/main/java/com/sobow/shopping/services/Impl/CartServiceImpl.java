package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;
import com.sobow.shopping.exceptions.CartItemAlreadyExistsException;
import com.sobow.shopping.repositories.CartItemRepository;
import com.sobow.shopping.repositories.CartRepository;
import com.sobow.shopping.services.CartService;
import com.sobow.shopping.services.ProductService;
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
    
    @Override
    public Cart createOrGetCartForUser(long userId) {
        // check if cart exists by userId
        boolean cartExists = false;
        if (cartExists) {
            // find it and return
            return null;
        } else {
            Cart newCart = new Cart();
            return cartRepository.save(newCart);
        }
        
    }
    
    @Override
    public void removeCartForUser(long userId) {
        throw new UnsupportedOperationException("removeCartForUser is not implemented yet");
        // find cart by user id
        // delete it by id
    }
    
    @Override
    public Cart findCartById(long id) {
        return cartRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Cart with " + id + " not found"));
    }
    
    @Transactional
    @Override
    public CartItem createCartItem(long cartId, CartItemCreateRequest request) {
        Cart cart = findCartById(cartId);
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
    
    private CartItem findCartItemByCartIdAndId(long cartId, long itemId) {
        return cartItemRepository.findByCartIdAndId(cartId, itemId)
                                 .orElseThrow(() -> new EntityNotFoundException(
                                     "CartItem " + itemId + " not found in cart " + cartId));
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
        Cart cart = findCartById(cartId);
        cart.removeAllCartItems();
    }
    
}

