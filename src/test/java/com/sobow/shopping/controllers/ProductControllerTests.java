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
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductCreateRequest;
import com.sobow.shopping.domain.product.dto.ProductResponse;
import com.sobow.shopping.domain.product.dto.ProductUpdateRequest;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ProductService productService;
    
    @MockitoBean
    private Mapper<Product, ProductResponse> productResponseMapper;
    
    private static final String PRODUCTS_PATH = "/api/products";
    private static final String PRODUCTS_BY_ID_PATH = "/api/products/{id}";
    private static final String PRODUCTS_SEARCH_PATH = "/api/products/search";
    
    private final TestFixtures fixtures = new TestFixtures();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    
    
    @Nested
    @DisplayName("createProduct")
    class createProduct {
        
        @Test
        public void createProduct_should_Return201WithDto_when_ValidRequest() throws Exception {
            // Given
            ProductCreateRequest request = fixtures.productCreateRequest();
            Category category = fixtures.categoryEntity();
            Product saved = fixtures.productEntity();
            ProductResponse response = fixtures.productResponseOf(saved);
            
            category.addProductAndLink(saved);
            String json = objectMapper.writeValueAsString(request);
            
            when(productService.create(request)).thenReturn(saved);
            when(productResponseMapper.mapToDto(saved)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(post(PRODUCTS_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isCreated())
                   .andExpect(header().exists(HttpHeaders.LOCATION))
                   .andExpect(jsonPath("$.message").value("Created"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()))
                   .andExpect(jsonPath("$.data.brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data.price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data.availableQty").value(response.availableQty()))
                   .andExpect(jsonPath("$.data.description").value(response.description()))
                   .andExpect(jsonPath("$.data.categoryId").value(response.categoryId()));

        }
        
        @Test
        public void createProduct_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            // Given
            ProductCreateRequest invalidRequest =
                new ProductCreateRequest(null, "  ", new BigDecimal(0),
                                         -1, "", null
                );
            
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            // When & Then
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
            // Given
            ProductUpdateRequest request = fixtures.productUpdateRequest();
            
            Category category = fixtures.categoryEntity();
            Product updated = fixtures.productEntity();
            category.addProductAndLink(updated);
            
            ProductResponse response = fixtures.productResponseOf(updated);
            
            String json = objectMapper.writeValueAsString(request);
            
            when(productService.partialUpdateById(fixtures.productId(), request)).thenReturn(updated);
            when(productResponseMapper.mapToDto(updated)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(put(PRODUCTS_BY_ID_PATH, fixtures.productId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Updated"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()))
                   .andExpect(jsonPath("$.data.brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data.price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data.availableQty").value(response.availableQty()))
                   .andExpect(jsonPath("$.data.description").value(response.description()))
                   .andExpect(jsonPath("$.data.categoryId").value(response.categoryId()));
        }
        
        @Test
        public void updateProduct_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            // Given
            ProductCreateRequest invalidRequest =
                new ProductCreateRequest("   ", "  ", new BigDecimal(0),
                                         -1, "", -1L
                );
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            // When & Then
            mockMvc.perform(put(PRODUCTS_BY_ID_PATH, fixtures.productId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateProduct_should_Return400_when_ProductIdLessThanOne() throws Exception {
            // Given
            ProductCreateRequest request = fixtures.productCreateRequest();
            
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(PRODUCTS_BY_ID_PATH, fixtures.invalidId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateProduct_should_Return404_when_ProductIdDoesNotExist() throws Exception {
            // Given
            ProductUpdateRequest request = fixtures.productUpdateRequest();
            
            when(productService.partialUpdateById(fixtures.nonExistingId(), request))
                .thenThrow(new EntityNotFoundException());
            
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(PRODUCTS_BY_ID_PATH, fixtures.nonExistingId())
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
            // Given
            Category category = fixtures.categoryEntity();
            Product product = fixtures.productEntity();
            Image image = fixtures.imageEntity();
            
            category.addProductAndLink(product);
            product.addImageAndLink(image);
            
            ProductResponse response = fixtures.productResponseOf(product);
            
            when(productService.findAllWithCategoryAndImages()).thenReturn(List.of(product));
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(PRODUCTS_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()))
                   .andExpect(jsonPath("$.data[0].brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data[0].price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data[0].availableQty").value(response.availableQty()))
                   .andExpect(jsonPath("$.data[0].description").value(response.description()))
                   .andExpect(jsonPath("$.data[0].categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data[0].imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void getAllProducts_should_Return200WithEmptyList_when_ProductsDoesNotExist() throws Exception {
            // Given
            when(productService.findAll()).thenReturn(List.of());
            
            // When & Then
            mockMvc.perform(get(PRODUCTS_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$.data", hasSize(0)));
        }
        
        @Test
        public void getProduct_should_Return200WithDto_when_ProductIdValid() throws Exception {
            // Given
            Category category = fixtures.categoryEntity();
            Product product = fixtures.productEntity();
            Image image = fixtures.imageEntity();
            
            category.addProductAndLink(product);
            product.addImageAndLink(image);
            
            ProductResponse response = fixtures.productResponseOf(product);
            
            when(productService.findWithCategoryAndImagesById(fixtures.productId())).thenReturn(product);
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(PRODUCTS_BY_ID_PATH, fixtures.productId()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()))
                   .andExpect(jsonPath("$.data.brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data.price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data.availableQty").value(response.availableQty()))
                   .andExpect(jsonPath("$.data.description").value(response.description()))
                   .andExpect(jsonPath("$.data.categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data.imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void getProduct_should_Return400_when_ProductIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(get(PRODUCTS_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void getProduct_should_Return404_when_ProductIdDoesNotExist() throws Exception {
            // Given
            when(productService.findWithCategoryAndImagesById(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            mockMvc.perform(get(PRODUCTS_BY_ID_PATH, fixtures.nonExistingId()))
                   .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("getWithFilters")
    class getWithFilters {
        
        @Test
        public void searchProducts_should_Return200WithDto_when_FilterByNameOnly() throws Exception {
            // Given
            Category category = fixtures.categoryEntity();
            Product product = fixtures.productEntity();
            Image image = fixtures.imageEntity();
            
            category.addProductAndLink(product);
            product.addImageAndLink(image);
            
            ProductResponse response = fixtures.productResponseOf(product);
            
            when(productService.search(product.getName(), null, null)).thenReturn(List.of(product));
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(PRODUCTS_SEARCH_PATH)
                                .param("name", product.getName()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()))
                   .andExpect(jsonPath("$.data[0].brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data[0].price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data[0].availableQty").value(response.availableQty()))
                   .andExpect(jsonPath("$.data[0].description").value(response.description()))
                   .andExpect(jsonPath("$.data[0].categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data[0].imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void searchProducts_should_Return200WithDto_when_FilterByBrandOnly() throws Exception {
            // Given
            Category category = fixtures.categoryEntity();
            Product product = fixtures.productEntity();
            Image image = fixtures.imageEntity();
            
            category.addProductAndLink(product);
            product.addImageAndLink(image);
            
            ProductResponse response = fixtures.productResponseOf(product);
            
            when(productService.search(null, product.getBrandName(), null)).thenReturn(List.of(product));
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(PRODUCTS_SEARCH_PATH)
                                .param("brandName", product.getBrandName()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()))
                   .andExpect(jsonPath("$.data[0].brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data[0].price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data[0].availableQty").value(response.availableQty()))
                   .andExpect(jsonPath("$.data[0].description").value(response.description()))
                   .andExpect(jsonPath("$.data[0].categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data[0].imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void searchProducts_should_Return200WithDto_when_FilterByCategoryOnly() throws Exception {
            // Given
            Category category = fixtures.categoryEntity();
            Product product = fixtures.productEntity();
            Image image = fixtures.imageEntity();
            
            category.addProductAndLink(product);
            product.addImageAndLink(image);
            
            ProductResponse response = fixtures.productResponseOf(product);
            
            when(productService.search(null, null, product.getCategory().getName())).thenReturn(List.of(product));
            when(productResponseMapper.mapToDto(product)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(PRODUCTS_SEARCH_PATH)
                                .param("categoryName", product.getCategory().getName()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()))
                   .andExpect(jsonPath("$.data[0].brandName").value(response.brandName()))
                   .andExpect(jsonPath("$.data[0].price").value(response.price().doubleValue()))
                   .andExpect(jsonPath("$.data[0].availableQty").value(response.availableQty()))
                   .andExpect(jsonPath("$.data[0].description").value(response.description()))
                   .andExpect(jsonPath("$.data[0].categoryId").value(response.categoryId()))
                   .andExpect(jsonPath("$.data[0].imagesId[0]").value(response.imagesId().get(0)));
        }
        
        @Test
        public void searchProducts_should_Return404WithEmptyList_when_NoProductsFound() throws Exception {
            // When & Then
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
            // When & Then
            mockMvc.perform(delete(PRODUCTS_BY_ID_PATH, fixtures.productId()))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        public void deleteProduct_should_Return400_when_ProductIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(delete(PRODUCTS_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
    }
}
