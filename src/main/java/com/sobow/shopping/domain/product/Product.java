package com.sobow.shopping.domain.product;

import com.sobow.shopping.config.MoneyConfig;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.exceptions.InvalidPriceException;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public Product(String name, String brandName, String description, BigDecimal price, int availableQty) {
        this.name = name;
        this.brandName = brandName;
        this.description = description;
        setPrice(price);
        setAvailableQty(availableQty);
    }
    
    // ---- Identifier ----------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ---- Basic columns -------------------------------------
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String brandName;
    
    @Column(nullable = false)
    private String description;
    
    @Digits(integer = 17, fraction = 2)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer availableQty;
    
    // ---- Associations --------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    
    // ---- Domain methods ------------------------------------
    public void linkTo(Category category) {
        this.category = category;
    }
    
    public void addImageAndLink(Image img) {
        images.add(img);
        img.linkTo(this);
    }
    
    public void removeImage(Image img) {
        images.remove(img);
    }
    
    // ---- Setter methods ------------------------------------
    public void setName(String name) {
        this.name = name;
    }
    
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPrice(BigDecimal price) {
        BigDecimal normalized = price.setScale(MoneyConfig.SCALE, MoneyConfig.ROUNDING);
        if (normalized.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException(normalized);
        }
        
        this.price = normalized;
    }
    
    public void setAvailableQty(int newQty) {
        if (newQty < 0) throw new OverDecrementException(id, newQty);
        availableQty = newQty;
    }
}
