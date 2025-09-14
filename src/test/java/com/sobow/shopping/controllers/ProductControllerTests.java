package com.sobow.shopping.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import com.sobow.shopping.domain.responses.ProductResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ProductService;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {
    
    private static final String PRODUCTS_PATH = "/api/products";
    private static final String PRODUCTS_BY_ID_PATH = "/api/products/{id}";
    private static final String PRODUCTS_SEARCH_PATH = "/api/products/search";
    
    private static final String EXISTING_PRODUCT_NAME = "existing product name";
    private static final String EXISTING_BRAND_NAME = "existing brand name";
    private static final String EXISTING_CATEGORY_NAME = "existing category name";
    
    private static final Long EXISTING_PRODUCT_ID = 100L;
    private static final Long NON_EXISTING_PRODUCT_ID = 999L;
    private static final Long INVALID_PRODUCT_ID = -1L;
    
    private TestFixtures fixtures = new TestFixtures();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
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
        public void createProduct_should_Return201WithDtoAndLocation_when_ValidRequest() throws Exception {
            fixtures.withEmptyImages();
            ProductCreateRequest request = fixtures.productCreateRequest();
            Product saved = fixtures.productEntity();
            ProductResponse response = fixtures.productResponse();
            
            String json = objectMapper.writeValueAsString(request);
            
            when(productService.save(request)).thenReturn(saved);
            when(productResponseMapper.mapToDto(saved)).thenReturn(response);
            
            mockMvc.perform(post(PRODUCTS_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isCreated())
                   .andExpect(header().string(HttpHeaders.LOCATION, PRODUCTS_PATH + "/" + saved.getId()))
                   .andExpect(jsonPath("$.message").value("Created"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()))
                   .andExpect(jsonPath("$.data.brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data.price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data.availableQuantity").value(response.availableQuantity()))
                   .andExpect(jsonPath("$.data.description").value(response.description()))
                   .andExpect(jsonPath("$.data.categoryId").value(response.categoryId()));

        }
        
        @Test
        public void createProduct_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            ProductCreateRequest invalidRequest =
                new ProductCreateRequest(null, "  ", new BigDecimal(0),
                                         -1, "", null
                );
            
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            mockMvc.perform(post(PRODUCTS_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("updateProduct")
    class updateProduct {
        
        @Test
        public void updateProduct_should_Return200WithDto_when_ValidRequest() throws Exception {
            ProductUpdateRequest request = fixtures.productUpdateRequest();
            Product updated = fixtures.productEntity();
            ProductResponse response = fixtures.productResponse();
            
            String json = objectMapper.writeValueAsString(request);
            
            when(productService.partialUpdateById(request, EXISTING_PRODUCT_ID)).thenReturn(updated);
            when(productResponseMapper.mapToDto(updated)).thenReturn(response);
            
            mockMvc.perform(put(PRODUCTS_BY_ID_PATH, EXISTING_PRODUCT_ID)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Updated"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()))
                   .andExpect(jsonPath("$.data.brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data.price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data.availableQuantity").value(response.availableQuantity()))
                   .andExpect(jsonPath("$.data.description").value(response.description()))
                   .andExpect(jsonPath("$.data.categoryId").value(response.categoryId()));
        }
        
        @Test
        public void updateProduct_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            ProductCreateRequest invalidRequest =
                new ProductCreateRequest("   ", "  ", new BigDecimal(0),
                                         -1, "", -1L
                );
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            mockMvc.perform(put(PRODUCTS_BY_ID_PATH, EXISTING_PRODUCT_ID)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateProduct_should_Return400_when_ProductIdLessThanOne() throws Exception {
            ProductCreateRequest request = fixtures.productCreateRequest();
            
            String json = objectMapper.writeValueAsString(request);
            
            mockMvc.perform(put(PRODUCTS_BY_ID_PATH, INVALID_PRODUCT_ID)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateProduct_should_Return404_when_ProductIdDoesNotExist() throws Exception {
            ProductUpdateRequest request = fixtures.productUpdateRequest();
            
            when(productService.partialUpdateById(request, NON_EXISTING_PRODUCT_ID))
                .thenThrow(new EntityNotFoundException());
            
            String json = objectMapper.writeValueAsString(request);
            
            mockMvc.perform(put(PRODUCTS_BY_ID_PATH, NON_EXISTING_PRODUCT_ID)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("getProducts")
    class getProducts {
        
        @Test
        public void getAllProducts_should_Return200WithList_when_ProductsExists() throws Exception {
            Product product = fixtures.productEntity();
            ProductResponse response = fixtures.productResponse();
            
            when(productService.findAllProductsWithCategoryAndImages()).thenReturn(List.of(product));
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            mockMvc.perform(get(PRODUCTS_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()))
                   .andExpect(jsonPath("$.data[0].brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data[0].price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data[0].availableQuantity").value(response.availableQuantity()))
                   .andExpect(jsonPath("$.data[0].description").value(response.description()))
                   .andExpect(jsonPath("$.data[0].categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data[0].imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void getAllProducts_should_Return200WithEmptyList_when_ProductsDoesNotExist() throws Exception {
            when(productService.findAll()).thenReturn(List.of());
            
            mockMvc.perform(get(PRODUCTS_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$.data", hasSize(0)));
        }
        
        @Test
        public void getProduct_should_Return200WithDto_when_ProductIdValid() throws Exception {
            Product product = fixtures.productEntity();
            ProductResponse response = fixtures.productResponse();
            
            when(productService.findProductWithCategoryAndImagesById(EXISTING_PRODUCT_ID)).thenReturn(product);
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            mockMvc.perform(get(PRODUCTS_BY_ID_PATH, EXISTING_PRODUCT_ID))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()))
                   .andExpect(jsonPath("$.data.brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data.price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data.availableQuantity").value(response.availableQuantity()))
                   .andExpect(jsonPath("$.data.description").value(response.description()))
                   .andExpect(jsonPath("$.data.categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data.imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void getProduct_should_Return400_when_ProductIdLessThanOne() throws Exception {
            mockMvc.perform(get(PRODUCTS_BY_ID_PATH, INVALID_PRODUCT_ID))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void getProduct_should_Return404_when_ProductIdDoesNotExist() throws Exception {
            when(productService.findProductWithCategoryAndImagesById(NON_EXISTING_PRODUCT_ID)).thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(get(PRODUCTS_BY_ID_PATH, NON_EXISTING_PRODUCT_ID))
                   .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("getWithFilters")
    class getWithFilters {
        
        @Test
        public void searchProducts_should_Return200WithDto_when_FilterByNameOnly() throws Exception {
            fixtures.withProductName(EXISTING_PRODUCT_NAME);
            Product product = fixtures.productEntity();
            ProductResponse response = fixtures.productResponse();
            
            when(productService.search(EXISTING_PRODUCT_NAME, null, null)).thenReturn(List.of(product));
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            mockMvc.perform(get(PRODUCTS_SEARCH_PATH)
                                .param("name", EXISTING_PRODUCT_NAME))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()))
                   .andExpect(jsonPath("$.data[0].brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data[0].price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data[0].availableQuantity").value(response.availableQuantity()))
                   .andExpect(jsonPath("$.data[0].description").value(response.description()))
                   .andExpect(jsonPath("$.data[0].categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data[0].imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void searchProducts_should_Return200WithDto_when_FilterByBrandOnly() throws Exception {
            fixtures.withBrandName(EXISTING_BRAND_NAME);
            Product product = fixtures.productEntity();
            ProductResponse response = fixtures.productResponse();
            
            when(productService.search(null, EXISTING_BRAND_NAME, null)).thenReturn(List.of(product));
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            mockMvc.perform(get(PRODUCTS_SEARCH_PATH)
                                .param("brandName", EXISTING_BRAND_NAME))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()))
                   .andExpect(jsonPath("$.data[0].brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data[0].price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data[0].availableQuantity").value(response.availableQuantity()))
                   .andExpect(jsonPath("$.data[0].description").value(response.description()))
                   .andExpect(jsonPath("$.data[0].categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data[0].imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void searchProducts_should_Return200WithDto_when_FilterByCategoryOnly() throws Exception {
            fixtures.withCategoryName(EXISTING_CATEGORY_NAME);
            Product product = fixtures.productEntity();
            ProductResponse response = fixtures.productResponse();
            
            when(productService.search(null, null, EXISTING_CATEGORY_NAME)).thenReturn(List.of(product));
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            mockMvc.perform(get(PRODUCTS_SEARCH_PATH)
                                .param("categoryName", EXISTING_CATEGORY_NAME))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()))
                   .andExpect(jsonPath("$.data[0].brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data[0].price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data[0].availableQuantity").value(response.availableQuantity()))
                   .andExpect(jsonPath("$.data[0].description").value(response.description()))
                   .andExpect(jsonPath("$.data[0].categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data[0].imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void searchProducts_should_Return404WithEmptyList_when_NoProductsFound() throws Exception {
            
            mockMvc.perform(get(PRODUCTS_SEARCH_PATH))
                   .andExpect(status().isNotFound())
                   .andExpect(jsonPath("$.message").value("Not found"))
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$.data", hasSize(0)));
        }
    }
    
    @Nested
    @DisplayName("deleteProduct")
    class deleteProduct {
        
        @Test
        public void deleteProduct_should_Return204_when_Deleted() throws Exception {
            mockMvc.perform(delete(PRODUCTS_BY_ID_PATH, EXISTING_PRODUCT_ID))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        public void deleteProduct_should_Return400_when_ProductIdLessThanOne() throws Exception {
            mockMvc.perform(delete(PRODUCTS_BY_ID_PATH, INVALID_PRODUCT_ID))
                   .andExpect(status().isBadRequest());
        }
    }
}
