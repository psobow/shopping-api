package com.sobow.shopping.domain.entities;

import com.sobow.shopping.exceptions.OverDecrementException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uc_products_name_brand",
            columnNames = {"name", "brand_name"}
        )
    }
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String brandName;
    
    @Column(nullable = false)
    private String description;
    
    @Digits(integer = 17, fraction = 2)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    
    @Setter(AccessLevel.NONE)
    @Column(nullable = false)
    private Integer availableQty;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    
    public void addImageAndLink(Image img) {
        images.add(img);
        img.setProduct(this);
    }
    
    public void removeImageAndUnlink(Image img) {
        images.remove(img);
    }
    
    public void setAvailableQty(Integer newQty) {
        if (newQty < 0) throw new OverDecrementException(id, newQty);
        availableQty = newQty;
    }
}
