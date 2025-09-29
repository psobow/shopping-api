package com.sobow.shopping.controllers.category;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.category.dto.CategoryRequest;
import com.sobow.shopping.controllers.category.dto.CategoryResponse;
import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.mappers.category.CategoryResponseMapper;
import com.sobow.shopping.services.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
    name = "Category Management Controller",
    description = "API to manage categories by Admin"
)
public class CategoryManagementController {
    
    private final CategoryService categoryService;
    private final CategoryResponseMapper categoryResponseMapper;
    
    @Operation(
        summary = "Create a new category",
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "name": "CategoryName"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto> createCategory(
        @RequestBody @Valid CategoryRequest request
    ) {
        Category saved = categoryService.create(request);
        CategoryResponse response = categoryResponseMapper.mapToDto(saved);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId()))
                             .body(new ApiResponseDto("Created", response));
    }
    
    @Operation(
        summary = "Update category",
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "Request", value = """
                    {
                        "name": "newCategoryName"
                    }
                    """)
            )
        )
    )
    @Parameters({
        @Parameter(name = "id", required = true, description = "Category ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto> updateCategory(
        @PathVariable @Positive long id,
        @RequestBody @Valid CategoryRequest request
    ) {
        Category updated = categoryService.partialUpdateById(id, request);
        CategoryResponse response = categoryResponseMapper.mapToDto(updated);
        return ResponseEntity.ok(
            new ApiResponseDto("Updated", response)
        );
    }
    
    @Operation(
        summary = "Delete category",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameters({
        @Parameter(name = "id", required = true, description = "Category ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
