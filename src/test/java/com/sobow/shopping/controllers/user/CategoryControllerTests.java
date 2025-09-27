package com.sobow.shopping.controllers.user;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sobow.shopping.controllers.category.CategoryController;
import com.sobow.shopping.controllers.category.dto.CategoryResponse;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.mappers.category.CategoryResponseMapper;
import com.sobow.shopping.services.category.CategoryService;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CategoryService categoryService;
    
    @MockitoBean
    private CategoryResponseMapper categoryResponseMapper;
    
    private static final String CATEGORIES_PATH = "/api/categories";
    private static final String CATEGORIES_BY_ID_PATH = "/api/categories/{id}";
    
    private final TestFixtures fixtures = new TestFixtures();
    
    
    
    @Nested
    @DisplayName("getCategory")
    class getCategory {
        
        @Test
        public void getAllCategories_should_Return200WithList_when_CategoriesExists() throws Exception {
            // Given
            Category category = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            when(categoryService.findAll()).thenReturn(List.of(category));
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(CATEGORIES_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Found"))
                   .andExpect(jsonPath("$.data[0].id").value(response.id()))
                   .andExpect(jsonPath("$.data[0].name").value(response.name()));
        }
        
        @Test
        public void getAllCategories_should_Return200WithEmptyList_when_CategoriesDoesNotExist() throws Exception {
            // Given
            when(categoryService.findAll()).thenReturn(List.of());
            
            // When & Then
            mockMvc.perform(get(CATEGORIES_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$.data", hasSize(0)));
        }
        
        @Test
        public void getCategory_should_Return200WithDto_when_CategoryIdValid() throws Exception {
            // Given
            Category category = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            when(categoryService.findById(fixtures.categoryId())).thenReturn(category);
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(CATEGORIES_BY_ID_PATH, fixtures.categoryId()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()));
        }
        
        @Test
        public void getCategory_should_Return400_when_CategoryIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(get(CATEGORIES_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void getCategory_should_Return404_when_CategoryIdDoesNotExist() throws Exception {
            // Given
            when(categoryService.findById(fixtures.nonExistingId()))
                .thenThrow(new EntityNotFoundException());
            
            // When & Then
            mockMvc.perform(get(CATEGORIES_BY_ID_PATH, fixtures.nonExistingId()))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void getCategoryByName_should_Return200WithDto_when_CategoryWithNameExists() throws Exception {
            // Given
            Category category = fixtures.categoryEntity();
            CategoryResponse response = fixtures.categoryResponse();
            
            when(categoryService.findByName(category.getName())).thenReturn(category);
            when(categoryResponseMapper.mapToDto(category)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(CATEGORIES_PATH)
                                .param("name", category.getName()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.name").value(response.name()));
        }
        
        @Test
        public void getCategoryByName_should_Return404_when_CategoryWithNameDoesNotExist() throws Exception {
            // Given
            when(categoryService.findByName("non existing category name"))
                .thenThrow(new EntityNotFoundException());
            
            // When & Then
            mockMvc.perform(get(CATEGORIES_PATH)
                                .param("name", "non existing category name"))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void getCategoryByName_should_Return400_when_RequestParamBlank() throws Exception {
            // When & Then
            mockMvc.perform(get(CATEGORIES_PATH)
                                .param("name", "  "))
                   .andExpect(status().isBadRequest());
        }
    }
}
