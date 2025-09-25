package com.sobow.shopping.domain.user;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.user.requests.UserProfileUpdateRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public UserProfile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // ---- Identifier & Basic columns ------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    // ---- Associations --------------------------------------
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAddress address;
    
    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
    
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();
    
    // ---- Domain methods ------------------------------------
    
    public void updateFrom(UserProfileUpdateRequest patch) {
        Objects.requireNonNull(patch, "User profile patch must not be null");
        if (patch.firstName() != null) this.firstName = patch.firstName();
        if (patch.lastName() != null) this.lastName = patch.lastName();
        if (patch.userAddress() != null) this.address.updateFrom(patch.userAddress());
    }
    
    public void linkTo(User user) {
        this.user = user;
    }
    
    public void setAddressAndLink(UserAddress address) {
        this.address = address;
        address.linkTo(this);
    }
    
    public void removeAddress() {
        this.address = null;
    }
    
    public void setCartAndLink(Cart cart) {
        this.cart = cart;
        cart.linkTo(this);
    }
    
    public void removeCart() {
        cart.removeAllCartItems();
        this.cart = null;
    }
    
    public void addOrderAndLink(Order order) {
        orders.add(order);
        order.linkTo(this);
    }
    
    public void removeOrder(Order order) {
        orders.remove(order);
    }
    
    // ---- Equality (proxy-safe) -----------------------------
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                                   ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                                   : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                                      ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                                      : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserProfile that = (UserProfile) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
               ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
               : getClass().hashCode();
    }
}
