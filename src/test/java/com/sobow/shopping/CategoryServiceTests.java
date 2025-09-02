package com.sobow.shopping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CategoryServiceTests {
    
    private final CategoryService categoryService;
    
    @Autowired
    public CategoryServiceTests(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @Test
    public void createAndFindCategory() {
        Category category = new Category();
        category.setName("Laptops");
        
        Category saved = categoryService.save(category);
        
        Category laptops = categoryService.findByName("Laptops").get();
        
        assertEquals("Laptops", laptops.getName());
    }
}
