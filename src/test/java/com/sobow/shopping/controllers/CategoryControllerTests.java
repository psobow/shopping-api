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
import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.requests.CategoryRequest;
import com.sobow.shopping.domain.responses.CategoryResponse;
import com.sobow.shopping.exceptions.CategoryAlreadyExistsException;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CategoryService;
import com.sobow.shopping.utils.TestFixtures;
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
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CategoryService categoryService;
    
    @MockitoBean
    private Mapper<Category, CategoryResponse> categoryResponseMapper;
    
    @MockitoBean
    private Mapper<Category, CategoryRequest> categoryRequestMapper;
    
    private static final String CATEGORIES_PATH = "/api/categories";
    private static final String CATEGORIES_BY_ID_PATH = "/api/categories/{id}";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("createCategory")
    class createCategory {
        
        @Test
        public void createCategory_should_Return201WithDtoAndLocation_when_ValidRequest() throws Exception {
            CategoryRequest request = fixtures.categoryRequest();
            Category mapped = fixtures.withCategoryEmptyProducts().categoryEntity();
            Category saved = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.save(mapped)).thenReturn(saved);
            when(categoryResponseMapper.mapToDto(saved)).thenReturn(response);
            
            mockMvc.perform(post(CATEGORIES_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isCreated())
                   .andExpect(header().string(HttpHeaders.LOCATION, CATEGORIES_PATH + "/" + saved.getId()))
                   .andExpect(jsonPath("$.message").value("Created"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()));
        }
        
        @Test
        public void createCategory_should_Return400_when_RequestBodyViolatesDtoConstraints() throws Exception {
            CategoryRequest invalidRequest = new CategoryRequest(null);
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            mockMvc.perform(post(CATEGORIES_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void createCategory_should_Return409_when_CategoryNameAlreadyExists() throws Exception {
            CategoryRequest request = fixtures.categoryRequest();
            Category mapped = fixtures.categoryEntity();
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.save(mapped)).thenThrow(
                new CategoryAlreadyExistsException(mapped.getName()));
            
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
        public void updateCategory_should_Return200WithDto_when_ValidRequest() throws Exception {
            CategoryRequest request = fixtures.categoryRequest();
            Category mapped = fixtures.categoryEntity();
            Category updated = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            String json = objectMapper.writeValueAsString(request);
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.partialUpdateById(mapped, fixtures.categoryId())).thenReturn(updated);
            when(categoryResponseMapper.mapToDto(updated)).thenReturn(response);
            
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
            CategoryRequest invalidRequest = new CategoryRequest(null);
            
            String json = objectMapper.writeValueAsString(invalidRequest);
            
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.categoryId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            CategoryRequest request = fixtures.categoryRequest();
            String json = objectMapper.writeValueAsString(request);
            
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.invalidId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateCategory_should_Return404_when_CategoryIdDoestNotExist() throws Exception {
            CategoryRequest request = fixtures.categoryRequest();
            Category mapped = fixtures.categoryEntity();
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.partialUpdateById(mapped, fixtures.nonExistingId()))
                .thenThrow(new EntityNotFoundException());
            
            String json = objectMapper.writeValueAsString(request);
            
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.nonExistingId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void updateCategory_should_Return409_when_CategoryNameAlreadyExists() throws Exception {
            CategoryRequest request = fixtures.categoryRequest();
            Category mapped = fixtures.categoryEntity();
            
            when(categoryRequestMapper.mapToEntity(request)).thenReturn(mapped);
            when(categoryService.partialUpdateById(mapped, fixtures.categoryId()))
                .thenThrow(new CategoryAlreadyExistsException(mapped.getName()));
            
            String json = objectMapper.writeValueAsString(request);
            
            mockMvc.perform(put(CATEGORIES_BY_ID_PATH, fixtures.categoryId())
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
            Category category = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            when(categoryService.findAll()).thenReturn(List.of(category));
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            mockMvc.perform(get(CATEGORIES_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()));
        }
        
        @Test
        public void getAllCategories_should_Return200WithEmptyList_when_CategoriesDoesNotExist() throws Exception {
            when(categoryService.findAll()).thenReturn(List.of());
            
            mockMvc.perform(get(CATEGORIES_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$.data", hasSize(0)));
        }
        
        @Test
        public void getCategory_should_Return200WithDto_when_CategoryIdValid() throws Exception {
            Category category = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            when(categoryService.findById(fixtures.categoryId())).thenReturn(category);
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            mockMvc.perform(get(CATEGORIES_BY_ID_PATH, fixtures.categoryId()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()));
        }
        
        @Test
        public void getCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            mockMvc.perform(get(CATEGORIES_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void getCategory_should_Return404_when_CategoryIdDoesNotExist() throws Exception {
            when(categoryService.findById(fixtures.nonExistingId()))
                .thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(get(CATEGORIES_BY_ID_PATH, fixtures.nonExistingId()))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void getCategoryByName_should_Return200WithDto_when_CategoryWithNameExists() throws Exception {
            Category category = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            when(categoryService.findByName(category.getName())).thenReturn(category);
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            mockMvc.perform(get(CATEGORIES_PATH)
                                .param("name", category.getName()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()));
        }
        
        @Test
        public void getCategoryByName_should_Return404_when_CategoryWithNameDoesNotExist() throws Exception {
            when(categoryService.findByName("non existing category name"))
                .thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(get(CATEGORIES_PATH)
                                .param("name", "non existing category name"))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void getCategoryByName_should_Return400_when_RequestParamBlank() throws Exception {
            mockMvc.perform(get(CATEGORIES_PATH)
                                .param("name", "  "))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("deleteCategory")
    class deleteCategory {
        
        @Test
        public void deleteCategory_should_Return204_when_Deleted() throws Exception {
            mockMvc.perform(delete(CATEGORIES_BY_ID_PATH, fixtures.categoryId()))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        public void deleteCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            mockMvc.perform(delete(CATEGORIES_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
    }
}
