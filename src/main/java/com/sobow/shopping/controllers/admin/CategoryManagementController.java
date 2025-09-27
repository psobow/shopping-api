package com.sobow.shopping.controllers.admin;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.dto.CategoryRequest;
import com.sobow.shopping.domain.category.dto.CategoryResponse;
import com.sobow.shopping.mappers.category.CategoryResponseMapper;
import com.sobow.shopping.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin/categories")
public class CategoryManagementController {
    
    private final CategoryService categoryService;
    private final CategoryResponseMapper categoryResponseMapper;
    
    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(
        @RequestBody @Valid CategoryRequest request
    ) {
        Category saved = categoryService.create(request);
        CategoryResponse response = categoryResponseMapper.mapToDto(saved);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId()))
                             .body(new ApiResponse("Created", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
        @PathVariable @Positive long id,
        @RequestBody @Valid CategoryRequest request
    ) {
        Category updated = categoryService.partialUpdateById(id, request);
        CategoryResponse response = categoryResponseMapper.mapToDto(updated);
        return ResponseEntity.ok(
            new ApiResponse("Updated", response)
        );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
