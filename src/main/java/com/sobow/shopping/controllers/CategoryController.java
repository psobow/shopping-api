package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.requests.CategoryRequest;
import com.sobow.shopping.domain.requests.markers.Create;
import com.sobow.shopping.domain.requests.markers.Update;
import com.sobow.shopping.domain.responses.ApiResponse;
import com.sobow.shopping.domain.responses.CategoryResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CategoryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {
        List<CategoryResponse> categoryResponseList = categoryService.findAll()
                                                                     .stream()
                                                                     .map(categoryResponseMapper::mapToDto)
                                                                     .toList();
        
        return ResponseEntity.ok(new ApiResponse("Found", categoryResponseList));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse> addCategory(
        @RequestBody @Validated({Create.class, Update.class}) CategoryRequest request) {
        Category category = categoryRequestMapper.mapToEntity(request);
        Category saved = categoryService.save(category);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId()))
                             .body(new ApiResponse("Created", categoryResponseMapper.mapToDto(saved)));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
        @RequestBody @Validated(Update.class) CategoryRequest request,
        @PathVariable @Min(1) Long id) {
        Category updated = categoryService.partialUpdateById(categoryRequestMapper.mapToEntity(request), id);
        return ResponseEntity.ok(
            new ApiResponse("Updated", categoryResponseMapper.mapToDto(updated))
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategory(@PathVariable @Min(1) Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(new ApiResponse("Found", categoryResponseMapper.mapToDto(category)));
    }
    
    @GetMapping(params = "name")
    public ResponseEntity<ApiResponse> getCategoryByName(@RequestParam @NotBlank String name) {
        Category category = categoryService.findByName(name);
        return ResponseEntity.ok(new ApiResponse("Found", categoryResponseMapper.mapToDto(category)));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Min(1) Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
