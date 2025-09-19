package com.sobow.shopping.domain.order;

import com.sobow.shopping.config.MoneyConfig;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

@Getter
@NoArgsConstructor
@Entity
public class OrderItem {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public OrderItem(Integer requestedQty, String productName, String productBrandName, BigDecimal productPrice) {
        this.requestedQty = requestedQty;
        this.productName = productName;
        this.productBrandName = productBrandName;
        this.productPrice = productPrice;
    }
    
    // ---- Identifier ----------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ---- Basic columns -------------------------------------
    @Column(nullable = false)
    private Integer requestedQty;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false)
    private String productBrandName;
    
    @Column(nullable = false)
    private BigDecimal productPrice;
    
    @Column(nullable = false)
    private BigDecimal totalPrice;
    
    // ---- Lifecycle callbacks -------------------------------
    @PrePersist
    public void onCreate() {
        productPrice = productPrice.setScale(MoneyConfig.SCALE, MoneyConfig.ROUNDING);
        totalPrice = productPrice.multiply(BigDecimal.valueOf(requestedQty));
    }
    
    // ---- Associations --------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    // ---- Domain methods ------------------------------------
    public void linkTo(Order order) {
        this.order = order;
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
        OrderItem orderItem = (OrderItem) o;
        return getId() != null && Objects.equals(getId(), orderItem.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
               ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
               : getClass().hashCode();
    }
}
