package com.sobow.shopping.utils;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.requests.CategoryRequest;
import com.sobow.shopping.domain.responses.CategoryResponse;

public class CategoryFixtures {
    
    public long id = 1L;
    public String name = "categoryName";
    
    public static CategoryFixtures defaults() {
        return new CategoryFixtures();
    }
    
    // overrides
    public CategoryFixtures withId(long newId) {
        this.id = newId;
        return this;
    }
    
    public CategoryFixtures withName(String newName) {
        this.name = newName;
        return this;
    }
    
    // layer objects
    public CategoryRequest request() {
        return new CategoryRequest(name);
    }
    
    public Category entity() {
        return new Category(id, name, null);
    }
    
    public CategoryResponse response() {
        return new CategoryResponse(id, name);
    }
}
