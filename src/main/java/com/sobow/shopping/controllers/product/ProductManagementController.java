package com.sobow.shopping.controllers.product;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.product.dto.ProductCreateRequest;
import com.sobow.shopping.controllers.product.dto.ProductResponse;
import com.sobow.shopping.controllers.product.dto.ProductUpdateRequest;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.mappers.product.ProductResponseMapper;
import com.sobow.shopping.services.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("${api.prefix}/admin/products")
@Tag(
    name = "Product Management Controller",
    description = "API to manage products by Admin"
)
public class ProductManagementController {
    
    private final ProductService productService;
    private final ProductResponseMapper productResponseMapper;
    
    @Operation(
        summary = "Create a new product",
        security = {@SecurityRequirement(name = "bearerAuth")},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "name": "productName",
                        "brandName": "brandName",
                        "price": 10.5,
                        "availableQuantity": 5,
                        "description": "description",
                        "categoryId": 1
                    }
                    """)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden (admin only)")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto> createProduct(
        @RequestBody @Valid ProductCreateRequest request
    ) {
        Product saved = productService.create(request);
        ProductResponse response = productResponseMapper.mapToDto(saved);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
                             .body(new ApiResponseDto("Created", response));
    }
    
    @Operation(
        summary = "Partially update product by id",
        security = {@SecurityRequirement(name = "bearerAuth")},
        parameters = {
            @Parameter(name = "id", description = "Product id", required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "name": "new product name",
                      "brandName": "new brand name",
                      "price": 20,
                      "availableQuantity": 15,
                      "description": "Updated description",
                      "categoryId": 2
                    }
                    """)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden (admin only)"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto> updateProduct(
        @RequestBody @Valid ProductUpdateRequest request,
        @PathVariable @Positive long id
    ) {
        Product updated = productService.partialUpdateById(id, request);
        ProductResponse response = productResponseMapper.mapToDto(updated);
        return ResponseEntity.ok(new ApiResponseDto("Updated", response));
    }
    
    @Operation(
        summary = "Delete product by id",
        security = {@SecurityRequirement(name = "bearerAuth")},
        parameters = {
            @Parameter(name = "id", description = "Product id", required = true)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted"),
        @ApiResponse(responseCode = "400", description = "Invalid id"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden (admin only)"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
