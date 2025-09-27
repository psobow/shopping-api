package com.sobow.shopping.domain.product;

import com.sobow.shopping.controllers.product.dto.ProductUpdateRequest;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.image.Image;
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
import java.util.Objects;
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
        this.price = price;
        setAvailableQty(availableQty);
    }
    
    // ---- Identifier & Basic columns ------------------------
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
    
    @Column(nullable = false)
    private Integer availableQty;
    
    // ---- Associations --------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    
    // ---- Domain methods ------------------------------------
    public void updateFrom(ProductUpdateRequest patch) {
        Objects.requireNonNull(patch, "Product patch must not be null");
        if (patch.name() != null) this.name = patch.name();
        if (patch.brandName() != null) this.brandName = patch.brandName();
        if (patch.description() != null) this.description = patch.description();
        if (patch.price() != null) this.price = patch.price();
        if (patch.availableQuantity() != null) this.availableQty = patch.availableQuantity();
    }
    
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
    public void setAvailableQty(int newQty) {
        if (newQty < 0) throw new OverDecrementException(id, newQty);
        availableQty = newQty;
    }
}
