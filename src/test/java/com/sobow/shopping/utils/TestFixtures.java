package com.sobow.shopping.utils;

import com.sobow.shopping.config.MoneyConfig;
import com.sobow.shopping.domain.cart.Cart;
import com.sobow.shopping.domain.cart.CartItem;
import com.sobow.shopping.domain.cart.CartItemCreateRequest;
import com.sobow.shopping.domain.cart.CartItemResponse;
import com.sobow.shopping.domain.cart.CartItemUpdateRequest;
import com.sobow.shopping.domain.cart.CartResponse;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.CategoryRequest;
import com.sobow.shopping.domain.category.CategoryResponse;
import com.sobow.shopping.domain.image.FileContent;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.ImageResponse;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.domain.order.OrderItemResponse;
import com.sobow.shopping.domain.order.OrderResponse;
import com.sobow.shopping.domain.order.OrderStatus;
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
import java.time.LocalDateTime;
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
    private BigDecimal productPrice = new BigDecimal("10.00");
    private Integer availableQuantity = 10;
    private String description = "product description";
    
    private Long imageId = 30L;
    private String fileName = "photo.png";
    private String fileType = "image/png";
    private byte[] byteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    private Blob blob;
    private String downloadUrl = "/api/images/" + imageId;
    
    private Long cartItemId = 40L;
    private Integer requestedQty = 1;
    private BigDecimal totalItemPrice =
        new BigDecimal(requestedQty).multiply(BigDecimal.valueOf(availableQuantity))
                                    .setScale(MoneyConfig.SCALE, MoneyConfig.ROUNDING);
    
    private Long cartId = 50L;
    private BigDecimal totalCartPrice = totalItemPrice;
    
    private Long userId = 60L;
    private String userFirstName = "first name";
    private String userLastName = "last name";
    private String userEmail = "user@email.com";
    private String userPassword = "password";
    
    private String cityName = "city name";
    private String streetName = "street name";
    private String streetNumber = "15";
    private String postCode = "11-222";
    
    private Long orderItemId = 70L;
    private Long orderId = 80L;
    
    public TestFixtures() {
        try {
            this.blob = new SerialBlob(byteArray);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Order orderEntity() {
        return new Order(OrderStatus.NEW);
    }
    
    public OrderResponse orderResponse() {
        return OrderResponse.builder()
                            .id(orderId)
                            .status(OrderStatus.NEW.toString())
                            .createdAt(LocalDateTime.now())
                            .totalOrderPrice(totalCartPrice)
                            .itemResponseList(List.of(orderItemResponse()))
                            .build();
    }
    
    public OrderItemResponse orderItemResponse() {
        return OrderItemResponse.builder()
                                .id(orderItemId)
                                .orderId(orderId)
                                .requestedQty(requestedQty)
                                .productName(productName)
                                .productBrandName(brandName)
                                .productPrice(productPrice)
                                .totalItemPrice(totalItemPrice)
                                .build();
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
    
    public CartResponse cartResponse() {
        CartItemResponse cartItemResponse = cartItemResponse();
        return new CartResponse(cartId, totalCartPrice, List.of(cartItemResponse));
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
    
    public CartItemResponse cartItemResponse() {
        return new CartItemResponse(cartItemId, productId, cartId, requestedQty, totalItemPrice);
    }
    
    public CartItemCreateRequest cartItemCreateRequest() {
        return new CartItemCreateRequest(productId, requestedQty);
    }
    
    public CartItemUpdateRequest cartItemUpdateRequest() {
        return new CartItemUpdateRequest(requestedQty);
    }
    
    public Product productEntity() {
        Product product = new Product(productName, brandName, description, productPrice, availableQuantity);
        return product;
    }
    
    public ProductCreateRequest productCreateRequest() {
        return new ProductCreateRequest(productName, brandName, productPrice, availableQuantity, description, categoryId);
    }
    
    public ProductUpdateRequest productUpdateRequest() {
        return new ProductUpdateRequest(productName, brandName, productPrice, availableQuantity, description, categoryId);
    }
    
    public ProductResponse productResponseOf(Product p) {
        return new ProductResponse(productId, productName, brandName, productPrice, availableQuantity, description,
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
    
    public Long orderId() {
        return orderId;
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

