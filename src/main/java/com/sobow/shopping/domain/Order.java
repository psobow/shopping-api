package com.sobow.shopping.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDateTime;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItem> orderItems = new HashSet<>();
    
    public BigDecimal getTotalPrice() {
        return orderItems.stream()
                         .map(OrderItem::getTotalPrice)
                         .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void addItemAndLink(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
    
    public void removeItemAndUnlink(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
    
    @PrePersist
    public void onCreate() {
        orderDateTime = LocalDateTime.now();
    }
}
