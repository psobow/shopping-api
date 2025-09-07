package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.requests.CategoryCreateRequest;
import com.sobow.shopping.domain.responses.ApiResponse;
import com.sobow.shopping.domain.responses.CategoryResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    private final Mapper<Category, CategoryResponse> categoryResponseMapper;
    private final Mapper<Category, CategoryCreateRequest> categoryRequestMapper;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {
        List<CategoryResponse> categoryResponseList = categoryService.findAll()
                                                                     .stream()
                                                                     .map(categoryResponseMapper::mapToDto)
                                                                     .toList();
        
        return ResponseEntity.ok(new ApiResponse("Found categories", categoryResponseList));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse> addCategory(@RequestBody CategoryCreateRequest dto) {
        Category category = categoryRequestMapper.mapToEntity(dto);
        Category saved = categoryService.save(category);
        return new ResponseEntity<>(
            new ApiResponse("Created", categoryResponseMapper.mapToDto(saved)),
            HttpStatus.CREATED
        );
    }
}
