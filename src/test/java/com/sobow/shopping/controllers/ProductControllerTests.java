package com.sobow.shopping.controllers;

import static org.assertj.core.api.Assertions.fail;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.responses.ProductResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ProductService productService;
    
    @MockitoBean
    private Mapper<Product, ProductResponse> productResponseMapper;
    
    @Nested
    @DisplayName("createProduct")
    class createProduct {
        
        @Test
        public void createProduct_should_Return201WithDtoAndLocation_when_ValidRequest() {
            fail("TODO: implement me");
        }
        
        @Test
        public void createProduct_should_Return400_when_RequestBodyViolatesDtoConstraints() {
            fail("TODO: implement me");
        }
    }
    
    @Nested
    @DisplayName("updateProduct")
    class updateProduct {
        
        @Test
        public void updateProduct_should_Return200WithDto_when_ValidRequest() {
            fail("TODO: implement me");
        }
        
        @Test
        public void updateProduct_should_Return400_when_RequestBodyViolatesDtoConstraints() {
            fail("TODO: implement me");
        }
        
        @Test
        public void updateProduct_should_Return400_when_ProductIdLessThanOne() {
            fail("TODO: implement me");
        }
        
        @Test
        public void updateProduct_should_Return404_when_ProductIdDoesNotExist() {
            fail("TODO: implement me");
        }
    }
    
    @Nested
    @DisplayName("getProducts")
    class getProducts {
        
        @Test
        public void getAllProducts_should_Return200WithList_when_ProductsExists() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getAllProducts_should_Return200WithEmptyList_when_ProductsDoesNotExist() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getProduct_should_Return200WithDto_when_ProductIdValid() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getProduct_should_Return400_when_ProductIdLessThanOne() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getProduct_should_Return404_when_ProductIdDoesNotExist() {
            fail("TODO: implement me");
        }
    }
    
    @Nested
    @DisplayName("deleteProduct")
    class deleteProduct {
        
        @Test
        public void deleteProduct_should_Return204_when_Deleted() {
            fail("TODO: implement me");
        }
        
        @Test
        public void deleteProduct_should_Return400_when_ProductIdLessThanOne() {
            fail("TODO: implement me");
        }
    }
}
