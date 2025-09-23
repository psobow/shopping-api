package com.sobow.shopping.controllers.user;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.dto.CategoryResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CategoryService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    private final Mapper<Category, CategoryResponse> categoryResponseMapper;
    
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
    
}
