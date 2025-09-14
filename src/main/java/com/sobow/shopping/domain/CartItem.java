package com.sobow.shopping.domain;

import com.sobow.shopping.exceptions.InsufficientStockException;
import com.sobow.shopping.exceptions.OverDecrementException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
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
    
    @NotNull
    @PositiveOrZero
    private Integer quantity = 0;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;
    
    public int setQuantity(int requestedQty) {
        if (product == null) {
            throw new IllegalStateException("CartItem has no product linked.");
        }
        
        int available = product.getAvailableQuantity();
        if (requestedQty > available) {
            throw new InsufficientStockException(product.getId(), available, requestedQty);
        }
        
        if (requestedQty < 0) {
            throw new OverDecrementException(product.getId(), quantity, requestedQty);
        }
        quantity = requestedQty;
        
        return requestedQty;
    }
    
    public BigDecimal unitPrice() {
        return product.getPrice().setScale(MONEY_SCALE, MONEY_ROUNDING);
    }
    
    public BigDecimal getTotalPrice() {
        return unitPrice()
            .multiply(BigDecimal.valueOf(quantity));
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
