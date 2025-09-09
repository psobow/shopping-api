package com.sobow.shopping.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.requests.CategoryRequest;
import com.sobow.shopping.domain.responses.CategoryResponse;
import com.sobow.shopping.exceptions.ResourceAlreadyExistsException;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
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

@WebMvcTest(CategoryController.class)
public class CategoryControllerTests {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CategoryService categoryService;
    
    @MockitoBean
    private Mapper<Category, CategoryResponse> categoryResponseMapper;
    
    @MockitoBean
    private Mapper<Category, CategoryRequest> categoryRequestMapper;
    
    @Nested
    @DisplayName("createCategory")
    class createCategory {
        
        @Test
        public void createCategory_should_Return201WithDtoAndLocation_when_ValidRequest() throws Exception {
            // Given
            Long newCategoryId = 1L;
            String name = "Valid name";
            
            CategoryRequest request = new CategoryRequest(name);
            Category mapped = new Category(null, name, null);
            Category saved = new Category(newCategoryId, name, null);
            CategoryResponse dto = new CategoryResponse(newCategoryId, name);
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.save(mapped)).thenReturn(saved);
            when(categoryResponseMapper.mapToDto(saved)).thenReturn(dto);
            
            // When + Then
            mockMvc.perform(post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isCreated())
                   .andExpect(header().string(HttpHeaders.LOCATION, "/api/categories/" + newCategoryId))
                   .andExpect(jsonPath("$.message").value("Created"))
                   .andExpect(jsonPath("$.data.id").value(newCategoryId))
                   .andExpect(jsonPath("$.data.name").value(name));
        }
        
        @Test
        public void createCategory_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            
            CategoryRequest invalidRequest = new CategoryRequest(null);
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            mockMvc.perform(post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
            
            verify(categoryService, never()).save(any());
        }
        
        @Test
        public void createCategory_should_Return409_when_CategoryNameAlreadyExists() throws Exception {
            String nameAlreadyExists = "nameAlreadyExists";
            CategoryRequest request = new CategoryRequest(nameAlreadyExists);
            Category mapped = new Category(null, nameAlreadyExists, null);
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.save(mapped)).thenThrow(
                new ResourceAlreadyExistsException("Category", "name", nameAlreadyExists));
            
            mockMvc.perform(post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isConflict());
        }
    }
    
    @Nested
    @DisplayName("updateCategory")
    class updateCategory {
        
        @Test
        public void updateCategory_should_Return200WithDto_when_ValidRequest() throws Exception {
            Long categoryId = 1L;
            String patchName = "Valid patch name";
            
            CategoryRequest request = new CategoryRequest(patchName);
            Category mapped = new Category(null, patchName, null);
            Category updated = new Category(categoryId, patchName, null);
            CategoryResponse dto = new CategoryResponse(categoryId, patchName);
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.partialUpdateById(mapped, categoryId)).thenReturn(updated);
            when(categoryResponseMapper.mapToDto(updated)).thenReturn(dto);
            
            mockMvc.perform(put("/api/categories/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Updated"))
                   .andExpect(jsonPath("$.data.id").value(categoryId))
                   .andExpect(jsonPath("$.data.name").value(patchName));
        }
        
        @Test
        public void updateCategory_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            Long categoryId = 1L;
            CategoryRequest invalidRequest = new CategoryRequest(null);
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            mockMvc.perform(put("/api/categories/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
            
            verify(categoryService, never()).save(any());
        }
        
        @Test
        public void updateCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            Long invalidCategoryId = -1L;
            CategoryRequest request = new CategoryRequest("Valid patch name");
            String json = objectMapper.writeValueAsString(request);
            
            mockMvc.perform(put("/api/categories/{id}", invalidCategoryId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
            
            verify(categoryService, never()).save(any());
        }
        
        @Test
        public void updateCategory_should_Return404_when_CategoryIdDoestNotExist() throws Exception {
            Long nonExistingId = 999L;
            CategoryRequest request = new CategoryRequest("Valid patch name");
            Category mapped = new Category(null, "Valid patch name", null);
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.partialUpdateById(mapped, nonExistingId)).thenThrow(new EntityNotFoundException());
            
            String json = objectMapper.writeValueAsString(request);
            
            mockMvc.perform(put("/api/categories/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void updateCategory_should_Return409_when_CategoryNameAlreadyExists() throws Exception {
            Long categoryId = 1L;
            String invalidPatchName = "Name already exists";
            CategoryRequest request = new CategoryRequest(invalidPatchName);
            Category mapped = new Category(null, invalidPatchName, null);
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.partialUpdateById(mapped, categoryId))
                .thenThrow(new ResourceAlreadyExistsException("Category", "name", invalidPatchName));
            
            String json = objectMapper.writeValueAsString(request);
            
            mockMvc.perform(put("/api/categories/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isConflict());
        }
    }
    
    @Nested
    @DisplayName("getCategory")
    class getCategory {
        
        @Test
        public void getAllCategories_should_Return200WithList_when_CategoriesExists() throws Exception {
            
            Category category = new Category(1L, "name", null);
            CategoryResponse response = new CategoryResponse(1L, "name");
            
            when(categoryService.findAll()).thenReturn(List.of(category));
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            mockMvc.perform(get("/api/categories"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data[0].id").value(1))
                   .andExpect(jsonPath("$.data[0].name").value("name"));
        }
        
        @Test
        public void getAllCategories_should_Return200WithEmptyList_when_CategoriesDoesNotExist() throws Exception {
            when(categoryService.findAll()).thenReturn(List.of());
            
            mockMvc.perform(get("/api/categories"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$.data", hasSize(0)));
        }
        
        @Test
        public void getCategory_should_Return200WithDto_when_CategoryIdValid() throws Exception {
            Long categoryId = 1L;
            String categoryName = "name";
            Category category = new Category(categoryId, categoryName, null);
            CategoryResponse response = new CategoryResponse(categoryId, categoryName);
            
            when(categoryService.findById(categoryId)).thenReturn(category);
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            mockMvc.perform(get("/api/categories/{id}", categoryId))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(categoryId))
                   .andExpect(jsonPath("$.data.name").value(categoryName));
        }
        
        @Test
        public void getCategory_should_Return404_when_CategoryIdDoesNotExist() throws Exception {
            Long nonExistingId = 999L;
            when(categoryService.findById(nonExistingId)).thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(get("/api/categories/{id}", nonExistingId))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void getCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            Long invalidId = -1L;
            
            mockMvc.perform(get("/api/categories/{id}", invalidId))
                   .andExpect(status().isBadRequest());
            
            verify(categoryService, never()).findById(any());
        }
        
        @Test
        public void getCategoryByName_should_Return200WithDto_when_CategoryNameExists() throws Exception {
            Long categoryId = 1L;
            String categoryName = "categoryName";
            
            Category category = new Category(categoryId, categoryName, null);
            CategoryResponse response = new CategoryResponse(categoryId, categoryName);
            
            when(categoryService.findByName(categoryName)).thenReturn(category);
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            mockMvc.perform(get("/api/categories")
                                .param("name", categoryName))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(categoryId))
                   .andExpect(jsonPath("$.data.name").value(categoryName));
        }
        
        @Test
        public void getCategoryByName_should_Return404_when_CategoryNameDoesNotExist() throws Exception {
            String categoryNameNonExisting = "nonExistingName";
            when(categoryService.findByName(categoryNameNonExisting)).thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(get("/api/categories")
                                .param("name", categoryNameNonExisting))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void getCategoryByName_should_Return400_when_CategoryNameBlank() throws Exception {
            String categoryNameBlank = "   ";
            mockMvc.perform(get("/api/categories")
                                .param("name", categoryNameBlank))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("deleteCategory")
    class deleteCategory {
        
        @Test
        public void deleteCategory_should_Return204_when_Deleted() throws Exception {
            Long categoryId = 1L;
            mockMvc.perform(delete("/api/categories/{id}", categoryId))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        public void deleteCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            Long invalidCategoryId = -1L;
            mockMvc.perform(delete("/api/categories/{id}", invalidCategoryId))
                   .andExpect(status().isBadRequest());
        }
    }
}
