package com.sobow.shopping.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();
    
    public BigDecimal getTotalPrice() {
        return cartItems.stream()
                        .map(CartItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void removeAllCartItems() {
        for (CartItem item : new HashSet<>(cartItems)) {
            removeCartItemAndUnlink(item);
        }
    }
    
    public void addCartItemAndLink(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
    }
    
    public void removeCartItemAndUnlink(CartItem item) {
        cartItems.remove(item);
        item.setCart(null);
    }
}
