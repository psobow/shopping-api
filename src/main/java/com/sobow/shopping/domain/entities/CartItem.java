package com.sobow.shopping.domain.entities;

import com.sobow.shopping.config.MoneyConfig;
import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.exceptions.OverDecrementException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "cart_item",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uc_cart_item_cart_product",
            columnNames = {"cart_id", "product_id"}
        )
    }
)
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Setter(AccessLevel.NONE)
    @Column(nullable = false)
    private Integer requestedQty;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    public int setRequestedQty(int requestedQty) {
        int availableQty = product.getAvailableQty();
        if (requestedQty > availableQty) {
            throw new InsufficientStockException(product.getId(), availableQty, requestedQty);
        }
        if (requestedQty < 0) {
            throw new OverDecrementException(product.getId(), requestedQty);
        }
        this.requestedQty = requestedQty;
        return requestedQty;
    }
    
    public BigDecimal unitPrice() {
        return product.getPrice()
                      .setScale(MoneyConfig.SCALE, MoneyConfig.ROUNDING);
    }
    
    public BigDecimal getTotalPrice() {
        return unitPrice().multiply(BigDecimal.valueOf(requestedQty));
    }
    
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
        CartItem cartItem = (CartItem) o;
        return getId() != null && Objects.equals(getId(), cartItem.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
               ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
               : getClass().hashCode();
    }
}
