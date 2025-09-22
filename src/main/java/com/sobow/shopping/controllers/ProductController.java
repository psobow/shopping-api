package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductCreateRequest;
import com.sobow.shopping.domain.product.dto.ProductResponse;
import com.sobow.shopping.domain.product.dto.ProductUpdateRequest;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("${api.prefix}/products")
public class ProductController {
    
    private final ProductService productService;
    private final Mapper<Product, ProductResponse> productResponseMapper;
    
    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(
        @RequestBody @Valid ProductCreateRequest request
    ) {
        Product saved = productService.create(request);
        ProductResponse response = productResponseMapper.mapToDto(saved);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
                             .body(new ApiResponse("Created", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
        @RequestBody @Valid ProductUpdateRequest request,
        @PathVariable @Positive long id
    ) {
        Product updated = productService.partialUpdateById(id, request);
        ProductResponse response = productResponseMapper.mapToDto(updated);
        return ResponseEntity.ok(new ApiResponse("Updated", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<ProductResponse> responseList = productService.findAllWithCategoryAndImages()
                                                           .stream()
                                                           .map(productResponseMapper::mapToDto)
                                                           .toList();
        
        return ResponseEntity.ok(new ApiResponse("Found", responseList));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable @Positive long id) {
        Product product = productService.findWithCategoryAndImagesById(id);
        ProductResponse response = productResponseMapper.mapToDto(product);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String brandName,
        @RequestParam(required = false) String categoryName
    ) {
        List<Product> foundProducts = productService.search(name, brandName, categoryName);
        
        List<ProductResponse> responseList = foundProducts.stream()
                                                          .map(productResponseMapper::mapToDto)
                                                          .toList();
        
        HttpStatus status = responseList.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        String message = responseList.isEmpty() ? "Not found" : "Found";
        
        return new ResponseEntity<>(new ApiResponse(message, responseList), status);
    }
}
