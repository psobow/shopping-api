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
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.mock.web.MockMultipartFile;

public class TestFixtures {
    
    public static final String MULTIPART_FORM_FIELD_NAME = "file";
    private Long nonExistingId = 999L;
    private Long invalidId = -1L;
    
    private Long categoryId = 10L;
    private String categoryName = "category name";
    
    private Long productId = 20L;
    private String productName = "product name";
    private String brandName = "brand name";
    private BigDecimal price = new BigDecimal("10.00");
    private Integer availableQuantity = 10;
    private String description = "product description";
    
    private Long imageId = 30L;
    private String fileName = "photo.png";
    private String fileType = "image/png";
    private byte[] byteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    private Blob blob;
    private String downloadUrl = "/api/images/" + imageId;
    
    private Long cartId = 40L;
    
    private Long cartItemId = 50L;
    private Integer requestedQty = 1;
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
        return cart;
    }
    
    public CartItem cartItem() {
        Category c = categoryEntity();
        Product p = productEntity();
        Image i = imageEntity();
        c.addProductAndLink(p);
        p.addImageAndLink(i);
        return new CartItem(cartItemId, cartItemQuantity, p, null);
    }
    
    public CartItemCreateRequest cartItemCreateRequest() {
        return new CartItemCreateRequest(productId, requestedQty);
    }
    
    public CartItemUpdateRequest cartItemUpdateRequest() {
        return new CartItemUpdateRequest(cartItemId, requestedQty);
    }
    
    public Product productEntity() {
        Product product = new Product(productId, productName, brandName, price,
                                      availableQuantity, description, null, new ArrayList<>());
        return product;
    }
    
    public ProductCreateRequest productCreateRequest() {
        return new ProductCreateRequest(productName, brandName, price, availableQuantity, description, categoryId);
    }
    
    public ProductUpdateRequest productUpdateRequest() {
        return new ProductUpdateRequest(productName, brandName, price, availableQuantity, description, categoryId);
    }
    
    public ProductResponse productResponseOf(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getBrandName(), p.getPrice(), p.getAvailableQuantity(), p.getDescription(),
                                   p.getCategory().getId(),
                                   p.getImages().stream().map(Image::getId).toList());
    }
    
    public Category categoryEntity() {
        Category category = new Category(categoryId, categoryName, new ArrayList<>());
        return category;
    }
    
    public CategoryRequest categoryRequest() {
        return new CategoryRequest(categoryName);
    }
    
    public CategoryResponse categoryResponse() {
        return new CategoryResponse(categoryId, categoryName);
    }
    
    public Image imageEntity() {
        Image image = new Image(imageId, fileName, fileType, blob, downloadUrl, null);
        return image;
    }
    
    public ImageResponse imageResponse() {
        return new ImageResponse(imageId, fileName, downloadUrl);
    }
    
    public MockMultipartFile multipartFile() {
        return new MockMultipartFile(MULTIPART_FORM_FIELD_NAME, fileName, fileType, byteArray);
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
    
    public TestFixtures withImageFile(Blob newBlob) {
        blob = newBlob;
        return this;
    }
    
    public TestFixtures withMultipartByteArray(byte[] newByteArray) {
        byteArray = newByteArray;
        return this;
    }
    
    public TestFixtures withRequestedQty(Integer newRequestedQty) {
        requestedQty = newRequestedQty;
        return this;
    }
}

