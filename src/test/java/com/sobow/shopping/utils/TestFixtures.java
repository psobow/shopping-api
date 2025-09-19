package com.sobow.shopping.utils;

import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.CartItemUpdateRequest;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.CategoryRequest;
import com.sobow.shopping.domain.category.CategoryResponse;
import com.sobow.shopping.domain.image.FileContent;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.ImageResponse;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.ProductCreateRequest;
import com.sobow.shopping.domain.product.ProductResponse;
import com.sobow.shopping.domain.product.ProductUpdateRequest;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.UserProfile;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
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
    
    private Long userId = 60L;
    private String userFirstName = "first name";
    private String userLastName = "last name";
    private String userEmail = "user@email.com";
    private String userPassword = "password";
    
    private String cityName = "city name";
    private String streetName = "street name";
    private String streetNumber = "15";
    private String postCode = "11-222";
    
    public TestFixtures() {
        try {
            this.blob = new SerialBlob(byteArray);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public UserAddress userAddressEntity() {
        return UserAddress.builder()
                          .cityName(cityName)
                          .streetName(streetName)
                          .streetNumber(streetNumber)
                          .postCode(postCode)
                          .build();
    }
    
    public UserProfile userProfileEntity() {
        UserProfile userProfile = new UserProfile(userFirstName, userLastName);
        return userProfile;
    }
    
    public User userEntity() {
        return new User(userEmail, userPassword);
    }
    
    public Cart cartEntity() {
        Cart cart = new Cart();
        return cart;
    }
    
    public CartItem cartItemEntity() {
        Category c = categoryEntity();
        Product p = productEntity();
        Image i = imageEntity();
        c.addProductAndLink(p);
        p.addImageAndLink(i);
        CartItem cartItem = CartItem.builder()
                                    .requestedQty(requestedQty)
                                    .product(p)
                                    .build();
        return cartItem;
    }
    
    public CartItemCreateRequest cartItemCreateRequest() {
        return new CartItemCreateRequest(productId, requestedQty);
    }
    
    public CartItemUpdateRequest cartItemUpdateRequest() {
        return new CartItemUpdateRequest(requestedQty);
    }
    
    public Product productEntity() {
        Product product = new Product(productName, brandName, description, price, availableQuantity);
        return product;
    }
    
    public ProductCreateRequest productCreateRequest() {
        return new ProductCreateRequest(productName, brandName, price, availableQuantity, description, categoryId);
    }
    
    public ProductUpdateRequest productUpdateRequest() {
        return new ProductUpdateRequest(productName, brandName, price, availableQuantity, description, categoryId);
    }
    
    public ProductResponse productResponseOf(Product p) {
        return new ProductResponse(productId, productName, brandName, price, availableQuantity, description,
                                   categoryId, List.of(imageId));
    }
    
    public Category categoryEntity() {
        Category category = new Category(categoryName);
        return category;
    }
    
    public CategoryRequest categoryRequest() {
        return new CategoryRequest(categoryName);
    }
    
    public CategoryResponse categoryResponse() {
        return new CategoryResponse(categoryId, categoryName);
    }
    
    public Image imageEntity() {
        Image image = new Image(fileName, fileType, blob);
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
    
    public Long cartId() {
        return cartId;
    }
    
    public Long cartItemId() {
        return cartItemId;
    }
    
    public Long userId() {
        return userId;
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
        downloadUrl = "/api/images/" + Objects.toString(newId, "");
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

