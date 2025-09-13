package com.sobow.shopping.utils;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import com.sobow.shopping.domain.responses.ProductResponse;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;

public class ProductFixtures {
    
    // Entities
    private Category category;
    private Long categoryId = 1L;
    private String categoryName = "categoryName";
    private List<Product> products = new ArrayList<>();
    
    private Product product;
    private Long productId = 2L;
    private String productName = "productName";
    private String brandName = "brandName";
    private BigDecimal price = new BigDecimal("10.00");
    private Integer availableQuantity = 5;
    private String description = "productDescription";
    private List<Image> images = new ArrayList<>();
    
    private Image image;
    private Long imageId = 3L;
    private String fileName = "photo.png";
    private String fileType = "image/png";
    private Blob blob = new SerialBlob(new byte[]{1, 2, 3});
    private String downloadUrl = "/api/images/" + imageId;
    
    private ProductFixtures() throws SQLException {
        this.category = new Category(categoryId, categoryName, products);
        this.product = new Product(productId, productName, brandName, price, availableQuantity, description, category, images);
        this.image = new Image(imageId, fileName, fileType, blob, downloadUrl, null);
        
        category.addProductAndLink(product);
        product.addImageAndLink(image);
    }
    
    // default factory
    public static ProductFixtures defaults() {
        try {
            return new ProductFixtures();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    // getters for each object
    public Product entity() {
        return product;
    }
    
    public ProductCreateRequest createRequest() {
        return new ProductCreateRequest(productName, brandName, price, availableQuantity, description, category.getId());
    }
    
    public ProductUpdateRequest updateRequest() {
        return new ProductUpdateRequest(productName, brandName, price, availableQuantity, description, category.getId());
    }
    
    public ProductResponse response() {
        return new ProductResponse(productId, productName, brandName, price, availableQuantity, description, category.getId(),
                                   images.stream().map(Image::getId).toList());
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
        product.removeImageAndUnlink(image);
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
}

