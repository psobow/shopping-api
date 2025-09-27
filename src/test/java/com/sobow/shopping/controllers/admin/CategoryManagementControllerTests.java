package com.sobow.shopping.controllers.admin;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobow.shopping.controllers.category.CategoryManagementController;
import com.sobow.shopping.controllers.category.dto.CategoryRequest;
import com.sobow.shopping.controllers.category.dto.CategoryResponse;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.exceptions.CategoryAlreadyExistsException;
import com.sobow.shopping.mappers.category.CategoryResponseMapper;
import com.sobow.shopping.services.category.CategoryService;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
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

@WebMvcTest(CategoryManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryManagementControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CategoryService categoryService;
    
    @MockitoBean
    private CategoryResponseMapper categoryResponseMapper;
    
    private static final String CATEGORIES_PATH = "/api/admin/categories";
    private static final String CATEGORIES_BY_ID_PATH = "/api/admin/categories/{id}";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("createCategory")
    class createCategory {
        
        @Test
        public void createCategory_should_Return201_when_ValidRequest() throws Exception {
            // Given
            CategoryRequest request = fixtures.categoryRequest();
            Category saved = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryService.create(request)).thenReturn(saved);
            when(categoryResponseMapper.mapToDto(saved)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(post(CATEGORIES_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isCreated())
                   .andExpect(header().exists(HttpHeaders.LOCATION))
                   .andExpect(jsonPath("$.message").value("Created"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()));
        }
        
        @Test
        public void createCategory_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            // Given
            CategoryRequest invalidRequest = new CategoryRequest(null);
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            // When & Then
            mockMvc.perform(post(CATEGORIES_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void createCategory_should_Return409_when_CategoryNameAlreadyExists() throws Exception {
            // Given
            CategoryRequest request = fixtures.categoryRequest();
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryService.create(request)).thenThrow(
                new CategoryAlreadyExistsException(request.name()));
            
            // When & Then
            mockMvc.perform(post(CATEGORIES_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isConflict());
        }
    }
    
    @Nested
    @DisplayName("updateCategory")
    class updateCategory {
        
        @Test
        public void updateCategory_should_Return200_when_ValidRequest() throws Exception {
            // Given
            CategoryRequest request = fixtures.categoryRequest();
            Category updated = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryService.partialUpdateById(fixtures.categoryId(), request)).thenReturn(updated);
            when(categoryResponseMapper.mapToDto(updated)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.categoryId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Updated"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()));
        }
        
        @Test
        public void updateCategory_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            // Given
            CategoryRequest invalidRequest = new CategoryRequest(null);
            
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            // When & Then
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.categoryId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            // Given
            CategoryRequest request = fixtures.categoryRequest();
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.invalidId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateCategory_should_Return404_when_CategoryIdDoestNotExist() throws Exception {
            // Given
            CategoryRequest request = fixtures.categoryRequest();
            
            when(categoryService.partialUpdateById(fixtures.nonExistingId(), request))
                .thenThrow(new EntityNotFoundException());
            
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.nonExistingId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void updateCategory_should_Return409_when_CategoryNameAlreadyExists() throws Exception {
            // Given
            CategoryRequest request = fixtures.categoryRequest();
            
            when(categoryService.partialUpdateById(fixtures.categoryId(), request))
                .thenThrow(new CategoryAlreadyExistsException(request.name()));
            
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.categoryId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isConflict());
        }
    }
    
    @Nested
    @DisplayName("deleteCategory")
    class deleteCategory {
        
        @Test
        public void deleteCategory_should_Return204_when_Deleted() throws Exception {
            // When & Then
            mockMvc.perform(delete(CATEGORIES_BY_ID_PATH, fixtures.categoryId()))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        public void deleteCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(delete(CATEGORIES_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
    }
}
