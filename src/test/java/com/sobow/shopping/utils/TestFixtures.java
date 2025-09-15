package com.sobow.shopping.utils;

import com.sobow.shopping.domain.Cart;
import com.sobow.shopping.domain.CartItem;
import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.dto.FileContent;
import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;
import com.sobow.shopping.domain.requests.CategoryRequest;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import com.sobow.shopping.domain.responses.CategoryResponse;
import com.sobow.shopping.domain.responses.ImageResponse;
import com.sobow.shopping.domain.responses.ProductResponse;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.mock.web.MockMultipartFile;

public class TestFixtures {
    
    private Long nonExistingId = 999L;
    private Long invalidId = -1L;
    
    private Long categoryId = 10L;
    private String categoryName = "category name";
    private boolean includeProductsInCategory = true;
    
    private Long productId = 20L;
    private String productName = "product name";
    private String brandName = "brand name";
    private BigDecimal price = new BigDecimal("10.00");
    private Integer availableQuantity = 10;
    private String description = "product description";
    private boolean includeImagesInProduct = true;
    
    private Long imageId = 30L;
    private String fileName = "photo.png";
    private String fileType = "image/png";
    private byte[] byteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    private Blob blob;
    private String downloadUrl = "/api/images/" + imageId;
    
    private Long cartId = 40L;
    private boolean includeItemsInCart = true;
    
    private Long cartItemId = 50L;
    private Integer cartItemQuantity = 1;
    
    public TestFixtures() {
        try {
            this.blob = new SerialBlob(byteArray);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Cart cart() {
        Cart cart = new Cart(cartId, new HashSet<>());
        
        if (includeItemsInCart) {
            CartItem item = cartItem();
            cart.addCartItemAndLink(item);
        }
        
        return cart;
    }
    
    public CartItem cartItem() {
        Product p = productEntity();
        return new CartItem(cartItemId, cartItemQuantity, p, null);
    }
    
    public CartItemCreateRequest cartItemCreateRequest() {
        return new CartItemCreateRequest(productId, 1);
    }
    
    public CartItemUpdateRequest cartItemUpdateRequest() {
        return new CartItemUpdateRequest(cartItemId, 2);
    }
    
    public Product productEntity() {
        Product product = new Product(productId, productName, brandName, price,
                                      availableQuantity, description, null, new ArrayList<>());
        
        Category category = new Category(categoryId, categoryName, new ArrayList<>());
        category.addProductAndLink(product);
        
        if (includeImagesInProduct) {
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
        
        if (includeImagesInProduct) imageIds.add(imageId);
        
        return new ProductResponse(productId, productName, brandName, price, availableQuantity, description,
                                   categoryId, imageIds);
    }
    
    public Category categoryEntity() {
        Category category = new Category(categoryId, categoryName, new ArrayList<>());
        
        if (includeProductsInCategory) {
            Product product = new Product(productId, productName, brandName, price,
                                          availableQuantity, description, null, new ArrayList<>());
            category.addProductAndLink(product);
            
            if (includeImagesInProduct) {
                Image image = new Image(imageId, fileName, fileType, blob, downloadUrl, null);
                product.addImageAndLink(image);
            }
        }
        return category;
    }
    
    public CategoryRequest categoryRequest() {
        return new CategoryRequest(categoryName);
    }
    
    public CategoryResponse categoryResponse() {
        return new CategoryResponse(categoryId, categoryName);
    }
    
    public Image imageEntity() {
        Product product = new Product(productId, productName, brandName, price,
                                      availableQuantity, description, null, new ArrayList<>());
        
        Category category = new Category(categoryId, categoryName, new ArrayList<>());
        category.addProductAndLink(product);
        
        Image image = new Image(imageId, fileName, fileType, blob, downloadUrl, null);
        product.addImageAndLink(image);
        
        return image;
    }
    
    public ImageResponse imageResponse() {
        return new ImageResponse(imageId, fileName, downloadUrl);
    }
    
    public MockMultipartFile multipartFile() {
        return new MockMultipartFile("file", fileName, fileType, byteArray);
    }
    
    public FileContent fileContent() {
        return new FileContent(fileName, fileType, byteArray.length, byteArray);
    }
    
    // getters
    public Long nonExistingId() {
        return nonExistingId;
    }
    
    public Long invalidId() {
        return invalidId;
    }
    
    public Long categoryId() {
        return categoryId;
    }
    
    public Long productId() {
        return productId;
    }
    
    public Long imageId() {
        return imageId;
    }
    
    // setters
    public TestFixtures withCategoryAndProductAndImageNullIds() {
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
        downloadUrl = "/api/images/" + imageId;
        return this;
    }
    
    public TestFixtures withProductName(String newName) {
        productName = newName;
        return this;
    }
    
    public TestFixtures withProductBrandName(String newBrandName) {
        brandName = newBrandName;
        return this;
    }
    
    public TestFixtures withCategoryName(String newCategoryName) {
        categoryName = newCategoryName;
        return this;
    }
    
    public TestFixtures withCategoryEmptyProducts() {
        includeProductsInCategory = false;
        return this;
    }
    
    public TestFixtures withProductEmptyImages() {
        includeImagesInProduct = false;
        return this;
    }
    
    public TestFixtures withCartEmptyItems() {
        includeItemsInCart = false;
        return this;
    }
    
    public TestFixtures withImageFile(Blob newBlob) {
        blob = newBlob;
        return this;
    }
    
    public TestFixtures withMultipartByteArray(byte[] newByteArray) {
        byteArray = newByteArray;
        return this;
    }
}

