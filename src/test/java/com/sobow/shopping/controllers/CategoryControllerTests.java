package com.sobow.shopping.controllers;

import com.sobow.shopping.services.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CategoryService categoryService;
    
    @Nested
    @DisplayName("getCategories")
    class getCategory {
        
        @Test
        public void getAllCategories_should_Return200WithList_when_CategoriesExists() {
        }
        
        @Test
        public void getAllCategories_should_Return200WithEmptyList_when_CategoriesDoesNotExist() {
        }
        
        @Test
        public void getCategory_should_Return200WithDto_when_CategoryIdValid() {
        }
        
        @Test
        public void getCategory_should_Return404_when_CategoryIdDoesNotExist() {
        }
        
        @Test
        public void getCategory_should_Return400_when_CategoryIdLessThanOne() {
        }
        
        @Test
        public void getCategoryByName_should_Return200WithDto_when_CategoryNameExists() {
        }
        
        @Test
        public void getCategoryByName_should_Return404_when_CategoryNameDoesNotExist() {
        }
        
        @Test
        public void getCategoryByName_should_Return400_when_CategoryNameBlank() {
        }
    }
    
    @Nested
    @DisplayName("addCategory")
    class addCategory {
        
        @Test
        public void addCategory_should_Return201WithDtoAndLocation_when_ValidRequest() {
        }
        
        @Test
        public void addCategory_should_Return400_when_RequestBodyIsInvalid() {
        }
        
        @Test
        public void addCategory_should_Return409_when_CategoryNameAlreadyExists() {
        }
    }
    
    @Nested
    @DisplayName("updateCategory")
    class updateCategory {
        
        @Test
        public void updateCategory_should_Return200WithDto_when_ValidRequest() {
        }
        
        @Test
        public void updateCategory_should_Return400_when_RequestBodyIsInvalid() {
        }
        
        @Test
        public void updateCategory_should_Return400_when_CategoryIdLessThanOne() {
        }
        
        @Test
        public void updateCategory_should_Return404_when_CategoryIdDoestNotExists() {
        }
        
        @Test
        public void updateCategory_should_Return409_when_CategoryNameAlreadyExists() {
        }
    }
    
    @Nested
    @DisplayName("deleteCategories")
    class deleteCategory {
        
        @Test
        public void deleteCategory_should_Return204_when_Deleted() {
        }
        
        @Test
        public void deleteCategory_should_Return400_when_CategoryIdLessThanOne() {
        }
    }
}
