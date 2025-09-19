package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.CategoryRequest;
import com.sobow.shopping.domain.category.CategoryResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    private final Mapper<Category, CategoryResponse> categoryResponseMapper;
    private final Mapper<Category, CategoryRequest> categoryRequestMapper;
    
    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(
        @RequestBody @Valid CategoryRequest request
    ) {
        Category mapped = categoryRequestMapper.mapToEntity(request);
        Category saved = categoryService.create(mapped);
        CategoryResponse response = categoryResponseMapper.mapToDto(saved);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId()))
                             .body(new ApiResponse("Created", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
        @RequestBody @Valid CategoryRequest request,
        @PathVariable @Positive long id
    ) {
        Category mapped = categoryRequestMapper.mapToEntity(request);
        Category updated = categoryService.partialUpdateById(mapped, id);
        CategoryResponse response = categoryResponseMapper.mapToDto(updated);
        return ResponseEntity.ok(
            new ApiResponse("Updated", response)
        );
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {
        List<CategoryResponse> responseList = categoryService.findAll()
                                                             .stream()
                                                             .map(categoryResponseMapper::mapToDto)
                                                             .toList();
        
        return ResponseEntity.ok(new ApiResponse("Found", responseList));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategory(@PathVariable @Positive long id) {
        Category category = categoryService.findById(id);
        CategoryResponse response = categoryResponseMapper.mapToDto(category);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @GetMapping(params = "name")
    public ResponseEntity<ApiResponse> getCategoryByName(@RequestParam @NotBlank String name) {
        Category category = categoryService.findByName(name);
        CategoryResponse response = categoryResponseMapper.mapToDto(category);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
