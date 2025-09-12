package com.sobow.shopping.utils;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import com.sobow.shopping.domain.responses.ProductResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductFixtures {
    
    private Long productId = 1L;
    private String productName = "productName";
    private String brandName = "brandName";
    private BigDecimal price = new BigDecimal("10.00");
    private Integer availableQuantity = 5;
    private String description = "productDescription";
    private Category category = new Category(2L, "categoryName", new ArrayList<>());
    
    private List<Image> images = List.of(
        new Image(3L, "photo.png", "image/png", null, null, null)
    );
    
    // default factory
    public static ProductFixtures defaults() {
        return new ProductFixtures();
    }
    
    // overrides
    public ProductFixtures withAllNullIds() {
        withProductId(null);
        withCategoryId(null);
        withImageId(null);
        return this;
    }
    
    public ProductFixtures withProductId(Long newId) {
        this.productId = newId;
        return this;
    }
    
    public ProductFixtures withCategoryId(Long newId) {
        this.category.setId(newId);
        return this;
    }
    
    public ProductFixtures withImageId(Long newId) {
        this.images.get(0).setId(newId);
        return this;
    }
    
    public ProductFixtures withProductName(String newName) {
        this.productName = newName;
        return this;
    }
    
    public ProductFixtures withEmptyImages() {
        this.images = new ArrayList<>();
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
    public ProductCreateRequest getNewCreateRequest() {
        return new ProductCreateRequest(productName, brandName, price, availableQuantity, description, category.getId());
    }
    
    public ProductUpdateRequest getNewUpdateRequest() {
        return new ProductUpdateRequest(productName, brandName, price, availableQuantity, description, category.getId());
    }
    
    public Product getNewEntity() {
        Product product = new Product(productId, productName, brandName, price, availableQuantity, description, category, images);
        
        if (images != null) {
            for (Image img : images) {
                img.setProduct(product);
            }
        }
        
        return product;
    }
    
    public ProductResponse getNewResponse() {
        return new ProductResponse(productId, productName, brandName, price, availableQuantity, description, category.getId(),
                                   images.stream().map(Image::getId).toList());
    }
}

