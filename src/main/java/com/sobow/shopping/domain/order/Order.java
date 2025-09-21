package com.sobow.shopping.domain.order;

import com.sobow.shopping.domain.user.UserProfile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "orders")
public class Order {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public Order(OrderStatus status) {
        this.status = status;
    }
    
    // ---- Identifier ----------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ---- Basic columns -------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private BigDecimal totalPrice;
    
    // ---- Lifecycle callbacks -------------------------------
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        totalPrice = orderItems.stream()
                               .map(OrderItem::getTotalPrice)
                               .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // ---- Associations --------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItem> orderItems = new HashSet<>();
    
    // ---- Domain methods ------------------------------------
    public void linkTo(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
    
    public void addItemAndLink(OrderItem item) {
        orderItems.add(item);
        item.linkTo(this);
    }
    
    public void removeItem(OrderItem item) {
        orderItems.remove(item);
    }
    
    // ---- Setter methods ------------------------------------
    public void setStatus(OrderStatus status) {
        this.status = status;
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
        Order order = (Order) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
               ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
               : getClass().hashCode();
    }
}
