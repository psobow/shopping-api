package com.sobow.shopping.domain.cart;

import com.sobow.shopping.domain.user.UserProfile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {
    
    // ---- Identifier & Basic columns ------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ---- Associations --------------------------------------
    @OneToOne(optional = false)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();
    
    // ---- Domain methods ------------------------------------
    public void linkTo(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
    
    public void addCartItemAndLink(CartItem item) {
        cartItems.add(item);
        item.linkTo(this);
    }
    
    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
    }
    
    public void removeAllCartItems() {
        for (CartItem item : new HashSet<>(cartItems)) {
            removeCartItem(item);
        }
    }
    
    // ---- Derived / non-persistent --------------------------
    public List<Long> getProductsId() {
        return cartItems.stream()
                        .map(item -> item.getProduct().getId())
                        .toList();
    }
    
    public BigDecimal getTotalPrice() {
        return cartItems.stream()
                        .map(CartItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
