package com.sobow.shopping.utils;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductRequest;
import com.sobow.shopping.domain.responses.ProductResponse;
import java.math.BigDecimal;

public class ProductFixtures {
    
    public long productId = 1L;
    public String productName = "productName";
    public String brandName = "brandName";
    public BigDecimal price = new BigDecimal("10.00");
    public int availableQuantity = 5;
    public String description = "productDescription";
    public Category category = new Category(2L, "categoryName", null);
    
    // default factory
    public static ProductFixtures defaults() {
        return new ProductFixtures();
    }
    
    // overrides
    public ProductFixtures withId(long newId) {
        this.productId = newId;
        return this;
    }
    
    public ProductFixtures withName(String newName) {
        this.productName = newName;
        return this;
    }
    
    public ProductFixtures withBrandName(String newBrandName) {
        this.brandName = newBrandName;
        return this;
    }
    
    public ProductFixtures withCategoryName(String newCategoryName) {
        this.category.setName(newCategoryName);
        return this;
    }
    
    // builders for each object
    public ProductRequest request() {
        return new ProductRequest(productName, brandName, price, availableQuantity, description, category.getId());
    }
    
    public Product entity() {
        return new Product(productId, productName, brandName, price, availableQuantity, description, category, null);
    }
    
    public ProductResponse response() {
        return new ProductResponse(productId, productName, brandName, price, availableQuantity, description, category.getId());
    }
}

