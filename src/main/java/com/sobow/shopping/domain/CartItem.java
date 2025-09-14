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
    
    public int incrementQuantity(int deltaQty) {
        requireLinkedProduct();
        requirePositiveDelta(deltaQty, "increment");
        return changeQuantity(deltaQty);          // +delta
    }
    
    public int decrementQuantity(int deltaQty) {
        requireLinkedProduct();
        requirePositiveDelta(deltaQty, "decrement");
        return changeQuantity(-deltaQty);         // -delta
    }
    
    private void requireLinkedProduct() {
        if (product == null) {
            throw new IllegalStateException("CartItem has no product linked.");
        }
    }
    
    private void requirePositiveDelta(int deltaQty, String action) {
        if (deltaQty <= 0) {
            throw new IllegalArgumentException("Quantity " + action + " must be a positive value.");
        }
    }
    
    private int changeQuantity(int delta) {
        int newQty = quantity + delta;
        
        if (delta > 0) { // increment branch
            int available = product.getAvailableQuantity();
            if (newQty > available) {
                throw new InsufficientStockException(product.getId(), available, delta, quantity);
            }
        } else {         // decrement branch
            if (newQty < 0) {
                throw new OverDecrementException(product.getId(), quantity, delta);
            }
        }
        
        quantity = newQty;
        return newQty;
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
