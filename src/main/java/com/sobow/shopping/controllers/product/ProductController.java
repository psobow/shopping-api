package com.sobow.shopping.controllers.product;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.product.dto.ProductResponse;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.services.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
@Tag(
    name = "Product Controller",
    description = "API to search, and fetch products by id or filters: name, brand, category"
)
public class ProductController {
    
    private final ProductService productService;
    
    @Operation(summary = "Get all products")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto> getAllProducts() {
        List<Product> products = productService.findAll();
        List<ProductResponse> responseList = productService.mapProductsToResponsesWithImageIds(products);
        return ResponseEntity.ok(new ApiResponseDto("Found", responseList));
    }
    
    @Operation(summary = "Get product by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "400", description = "Invalid id"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto> getProduct(@PathVariable @Positive long id) {
        Product product = productService.findById(id);
        List<ProductResponse> responseList = productService.mapProductsToResponsesWithImageIds(List.of(product));
        ProductResponse response = responseList.getFirst();
        return ResponseEntity.ok(new ApiResponseDto("Found", response));
    }
    
    @Operation(
        summary = "Search products",
        parameters = {
            @Parameter(name = "name", description = "Product name", required = false),
            @Parameter(name = "brandName", description = "Brand name", required = false),
            @Parameter(name = "categoryName", description = "Category name", required = false)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto> searchProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String brandName,
        @RequestParam(required = false) String categoryName
    ) {
        List<Product> products = productService.search(name, brandName, categoryName);
        List<ProductResponse> responseList = productService.mapProductsToResponsesWithImageIds(products);
        
        HttpStatus status = responseList.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        String message = responseList.isEmpty() ? "Not found" : "Found";
        
        return new ResponseEntity<>(new ApiResponseDto(message, responseList), status);
    }
}
