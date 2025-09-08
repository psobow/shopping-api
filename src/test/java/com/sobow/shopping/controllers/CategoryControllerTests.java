package com.sobow.shopping.controllers;

import static org.assertj.core.api.Assertions.fail;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.requests.CategoryRequest;
import com.sobow.shopping.domain.responses.CategoryResponse;
import com.sobow.shopping.mappers.Mapper;
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
    
    @MockitoBean
    private Mapper<Category, CategoryResponse> categoryResponseMapper;
    
    @MockitoBean
    private Mapper<Category, CategoryRequest> categoryRequestMapper;
    
    @Nested
    @DisplayName("getCategory")
    class getCategory {
        
        @Test
        public void getAllCategories_should_Return200WithList_when_CategoriesExists() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getAllCategories_should_Return200WithEmptyList_when_CategoriesDoesNotExist() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getCategory_should_Return200WithDto_when_CategoryIdValid() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getCategory_should_Return404_when_CategoryIdDoesNotExist() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getCategory_should_Return400_when_CategoryIdLessThanOne() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getCategoryByName_should_Return200WithDto_when_CategoryNameExists() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getCategoryByName_should_Return404_when_CategoryNameDoesNotExist() {
            fail("TODO: implement me");
        }
        
        @Test
        public void getCategoryByName_should_Return400_when_CategoryNameBlank() {
            fail("TODO: implement me");
        }
    }
    
    @Nested
    @DisplayName("createCategory")
    class createCategory {
        
        @Test
        public void createCategory_should_Return201WithDtoAndLocation_when_ValidRequest() {
            fail("TODO: implement me");
        }
        
        @Test
        public void createCategory_should_Return400_when_RequestBodyViolatesDtoConstraints() {
            fail("TODO: implement me");
        }
        
        @Test
        public void createCategory_should_Return409_when_CategoryNameAlreadyExists() {
            fail("TODO: implement me");
        }
    }
    
    @Nested
    @DisplayName("updateCategory")
    class updateCategory {
        
        @Test
        public void updateCategory_should_Return200WithDto_when_ValidRequest() {
            fail("TODO: implement me");
        }
        
        @Test
        public void updateCategory_should_Return400_when_RequestBodyViolatesDtoConstraints() {
            fail("TODO: implement me");
        }
        
        @Test
        public void updateCategory_should_Return400_when_CategoryIdLessThanOne() {
            fail("TODO: implement me");
        }
        
        @Test
        public void updateCategory_should_Return404_when_CategoryIdDoestNotExists() {
            fail("TODO: implement me");
        }
        
        @Test
        public void updateCategory_should_Return409_when_CategoryNameAlreadyExists() {
            fail("TODO: implement me");
        }
    }
    
    @Nested
    @DisplayName("deleteCategory")
    class deleteCategory {
        
        @Test
        public void deleteCategory_should_Return204_when_Deleted() {
            fail("TODO: implement me");
        }
        
        @Test
        public void deleteCategory_should_Return400_when_CategoryIdLessThanOne() {
            fail("TODO: implement me");
        }
    }
}
