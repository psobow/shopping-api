package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.Product;
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
    public Cart createCartForUser(Long userId) {
        throw new UnsupportedOperationException("createCartForUser is not implemented yet");
    }
    
    @Override
    public void removeCartForUser(Long userId) {
        throw new UnsupportedOperationException("removeCartForUser is not implemented yet");
    }
    
    @Override
    public Cart findCartById(Long id) {
        return cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart with " + id + " not found"));
    }
    
    // TODO: you can use CartItemDto as method argument instead of incomingCartItem
    @Transactional
    @Override
    public CartItem addCartItem(Long cartId, CartItem incomingItem) {
        Cart cart = findCartById(cartId);
        Product product = productService.findById(incomingItem.getProduct().getId());
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, product.getId())
                                          .orElseGet(() -> {
                                              CartItem newItem = new CartItem();
                                              newItem.setProduct(product);
                                              newItem.setQuantity(0);
                                              cart.addCartItemAndLink(newItem);
                                              return newItem;
                                          });
        
        item.incrementQuantity(incomingItem.getQuantity());
        return item;
    }
    
    @Transactional
    @Override
    public void removeCartItem(Long cartId, Long itemId) {
        CartItem item = findItemByCartIdAndId(cartId, itemId);
        Cart cart = item.getCart();
        cart.removeCartItemAndUnlink(item);
    }
    
    @Transactional
    @Override
    public void incrementCartItemQty(Long cartId, Long itemId) {
        CartItem item = findItemByCartIdAndId(cartId, itemId);
        item.incrementQuantity(1);
    }
    
    @Transactional
    @Override
    public void decrementCartItemQty(Long cartId, Long itemId) {
        CartItem item = findItemByCartIdAndId(cartId, itemId);
        int resultQuantity = item.decrementQuantity(1);
        if (resultQuantity == 0) removeCartItem(cartId, itemId);
    }
    
    @Transactional
    @Override
    public void removeAllCartItems(Long cartId) {
        Cart cart = findCartById(cartId);
        cart.removeAllCartItems();
    }
    
    @Override
    public BigDecimal getCartTotalPrice(Long cartId) {
        Cart cart = findCartById(cartId);
        return cart.getTotalPrice();
    }
    
    @Override
    public BigDecimal getCartItemTotalPrice(Long cartId, Long itemId) {
        CartItem item = findItemByCartIdAndId(cartId, itemId);
        return item.getTotalPrice();
    }
    
    private CartItem findItemByCartIdAndId(Long cartId, Long itemId) {
        return cartItemRepository.findByCartIdAndId(cartId, itemId)
                                 .orElseThrow(() -> new EntityNotFoundException(
                                     "CartItem " + itemId + " not found in cart " + cartId));
    }
}

