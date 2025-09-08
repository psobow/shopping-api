package com.sobow.shopping.controllers;

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
    @DisplayName("getProducts")
    class getProducts {
        
        @Test
        public void getAllProducts_should_Return200WithList_when_ProductsExists() {
        }
        
        @Test
        public void getAllProducts_should_Return200WithEmptyList_when_ProductsDoesNotExist() {
        }
        
        @Test
        public void getProduct_should_Return200WithDto_when_ProductIdValid() {
        }
        
        @Test
        public void getProduct_should_Return404_when_ProductIdDoesNotExist() {
        }
        
        @Test
        public void getProduct_should_Return400_when_ProductIdLessThanOne() {
        }
    }
}
