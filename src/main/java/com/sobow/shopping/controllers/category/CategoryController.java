package com.sobow.shopping.controllers.category;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.category.dto.CategoryResponse;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.mappers.category.CategoryResponseMapper;
import com.sobow.shopping.services.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
@Tag(
    name = "Category Controller",
    description = "API to get categories by id or name"
)
public class CategoryController {
    
    private final CategoryService categoryService;
    private final CategoryResponseMapper categoryResponseMapper;
    
    @Operation(
        summary = "List categories"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto> getAllCategories() {
        List<CategoryResponse> responseList = categoryService.findAll()
                                                             .stream()
                                                             .map(categoryResponseMapper::mapToDto)
                                                             .toList();
        
        return ResponseEntity.ok(new ApiResponseDto("Found", responseList));
    }
    
    @Operation(
        summary = "Get category by id"
    )
    @Parameters({
        @Parameter(name = "id", required = true, description = "Category ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto> getCategory(@PathVariable @Positive long id) {
        Category category = categoryService.findById(id);
        CategoryResponse response = categoryResponseMapper.mapToDto(category);
        return ResponseEntity.ok(new ApiResponseDto("Found", response));
    }
    
    @Operation(
        summary = "Get category by name"
    )
    @Parameters({
        @Parameter(name = "name", required = true, description = "Category name")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{name}")
    public ResponseEntity<ApiResponseDto> getCategoryByName(@PathVariable @NotBlank String name) {
        Category category = categoryService.findByName(name);
        CategoryResponse response = categoryResponseMapper.mapToDto(category);
        return ResponseEntity.ok(new ApiResponseDto("Found", response));
    }
    
}
