package com.sobow.shopping.utils;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.CategoryRequest;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import com.sobow.shopping.domain.responses.CategoryResponse;
import com.sobow.shopping.domain.responses.ProductResponse;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;

public class TestFixtures {
    
    private Long categoryId = 10L;
    private String categoryName = "categoryName";
    
    private Long productId = 20L;
    private String productName = "productName";
    private String brandName = "brandName";
    private BigDecimal price = new BigDecimal("10.00");
    private Integer availableQuantity = 5;
    private String description = "productDescription";
    private boolean includeImages = true;
    
    private Long imageId = 30L;
    private String fileName = "photo.png";
    private String fileType = "image/png";
    private Blob blob;
    private String downloadUrl = "/api/images/" + imageId;
    
    public TestFixtures() {
        try {
            this.blob = new SerialBlob(new byte[]{1, 2, 3});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Product productEntity() {
        Category category = new Category(categoryId, categoryName, new ArrayList<>());
        Product product = new Product(productId, productName, brandName, price,
                                      availableQuantity, description, null, new ArrayList<>());
        category.addProductAndLink(product);
        
        if (includeImages) {
            Image image = new Image(imageId, fileName, fileType, blob, downloadUrl, null);
            product.addImageAndLink(image);
        }
        
        return product;
    }
    
    public ProductCreateRequest productCreateRequest() {
        return new ProductCreateRequest(productName, brandName, price, availableQuantity, description, categoryId);
    }
    
    public ProductUpdateRequest productUpdateRequest() {
        return new ProductUpdateRequest(productName, brandName, price, availableQuantity, description, categoryId);
    }
    
    public ProductResponse productResponse() {
        List<Long> imageIds = new ArrayList<>();
        
        if (includeImages) imageIds.add(60L);
        
        return new ProductResponse(productId, productName, brandName, price, availableQuantity, description,
                                   categoryId, imageIds);
    }
    
    public Category categoryEntity() {
        return new Category(categoryId, categoryName, new ArrayList<>());
    }
    
    public CategoryRequest categoryRequest() {
        return new CategoryRequest(categoryName);
    }
    
    public CategoryResponse categoryResponse() {
        return new CategoryResponse(categoryId, categoryName);
    }
    
    public TestFixtures withAllNullIds() {
        withProductId(null);
        withCategoryId(null);
        withImageId(null);
        return this;
    }
    
    public TestFixtures withProductId(Long newId) {
        productId = newId;
        return this;
    }
    
    public TestFixtures withCategoryId(Long newId) {
        categoryId = newId;
        return this;
    }
    
    public TestFixtures withImageId(Long newId) {
        imageId = newId;
        return this;
    }
    
    public TestFixtures withProductName(String newName) {
        productName = newName;
        return this;
    }
    
    public TestFixtures withBrandName(String newBrandName) {
        brandName = newBrandName;
        return this;
    }
    
    public TestFixtures withCategoryName(String newCategoryName) {
        categoryName = newCategoryName;
        return this;
    }
    
    public TestFixtures withEmptyImages() {
        includeImages = false;
        return this;
    }
}

