package com.sobow.shopping.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sobow.shopping.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CategoryRepositoryIntegrationTests {
    
    @Autowired
    CategoryRepository categoryRepository;
    
    @Test
    void existsByNameAndIdNot_trueWhenOtherRowHasSameName() {
        Category books = new Category();
        books.setName("Books");
        Category electronics = new Category();
        electronics.setName("Electronics");
        
        // save in DB
        books = categoryRepository.save(books);
        electronics = categoryRepository.save(electronics);
        
        // another row named "Books" -> return true
        assertTrue(categoryRepository.existsByNameAndIdNot("Books", electronics.getId()));
        
        // only same row named "Books" -> return false
        assertFalse(categoryRepository.existsByNameAndIdNot("Books", books.getId()));
    }
}
