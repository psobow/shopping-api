package com.sobow.shopping.domain.category;

import com.sobow.shopping.domain.product.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public Category(String name) {
        this.name = name;
    }
    
    // ---- Identifier & Basic columns ------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    // ---- Associations --------------------------------------
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
    
    // ---- Domain methods ------------------------------------
    public void addProductAndLink(Product p) {
        products.add(p);
        p.linkTo(this);
    }
    
    public void removeProduct(Product p) {
        products.remove(p);
    }
    
    // ---- Setter methods ------------------------------------
    public void setName(String name) {
        this.name = name;
    }
}
