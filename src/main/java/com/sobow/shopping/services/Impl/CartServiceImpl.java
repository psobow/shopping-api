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
import java.math.BigDecimal;
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
    public Cart createCartForUser(long userId) {
        throw new UnsupportedOperationException("createCartForUser is not implemented yet");
    }
    
    @Override
    public void removeCartForUser(long userId) {
        throw new UnsupportedOperationException("removeCartForUser is not implemented yet");
    }
    
    @Override
    public Cart findCartById(long id) {
        return cartRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Cart with " + id + " not found"));
    }
    
    @Transactional
    @Override
    public CartItem createCartItem(long cartId, CartItemCreateRequest dto) {
        Cart cart = findCartById(cartId);
        Product product = productService.findById(dto.productId());
        
        boolean itemExists = cartItemRepository.existsByCartIdAndProductId(cartId, product.getId());
        if (itemExists) throw new CartItemAlreadyExistsException(cartId, product.getId());
        
        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(dto.requestedQty());
        cart.addCartItemAndLink(newItem);
        return newItem;
    }
    
    @Transactional
    @Override
    public void updateCartItemQty(long cartId, CartItemUpdateRequest dto) {
        CartItem item = findItemByCartIdAndId(cartId, dto.cartItemId());
        int resultQuantity = item.setQuantity(dto.requestedQty());
        if (resultQuantity == 0) removeCartItem(cartId, dto.cartItemId());
    }
    
    @Transactional
    @Override
    public void removeCartItem(long cartId, long itemId) {
        CartItem item = findItemByCartIdAndId(cartId, itemId);
        Cart cart = item.getCart();
        cart.removeCartItemAndUnlink(item);
    }
    
    @Transactional
    @Override
    public void removeAllCartItems(long cartId) {
        Cart cart = findCartById(cartId);
        cart.removeAllCartItems();
    }
    
    @Override
    public BigDecimal getCartTotalPrice(long cartId) {
        Cart cart = findCartById(cartId);
        return cart.getTotalPrice();
    }
    
    @Override
    public BigDecimal getCartItemTotalPrice(long cartId, long itemId) {
        CartItem item = findItemByCartIdAndId(cartId, itemId);
        return item.getTotalPrice();
    }
    
    private CartItem findItemByCartIdAndId(long cartId, long itemId) {
        return cartItemRepository.findByCartIdAndId(cartId, itemId)
                                 .orElseThrow(() -> new EntityNotFoundException(
                                     "CartItem " + itemId + " not found in cart " + cartId));
    }
}

