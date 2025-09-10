package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductRequest;
import com.sobow.shopping.domain.requests.markers.Create;
import com.sobow.shopping.domain.requests.markers.Update;
import com.sobow.shopping.domain.responses.ApiResponse;
import com.sobow.shopping.domain.responses.ProductResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ProductService;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("${api.prefix}/products")
public class ProductController {
    
    private final ProductService productService;
    private final Mapper<Product, ProductResponse> productResponseMapper;
    
    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(
        @RequestBody @Validated({Create.class, Update.class}) ProductRequest request) {
        Product saved = productService.save(request);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
                             .body(new ApiResponse("Created", productResponseMapper.mapToDto(saved)));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
        @RequestBody @Validated(Update.class) ProductRequest request,
        @PathVariable @Positive Long id) {
        Product updated = productService.partialUpdateById(request, id);
        return ResponseEntity.ok(
            new ApiResponse("Updated", productResponseMapper.mapToDto(updated))
        );
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<ProductResponse> productResponseList = productService.findAll()
                                                                  .stream()
                                                                  .map(productResponseMapper::mapToDto)
                                                                  .toList();
        
        return ResponseEntity.ok(new ApiResponse("Found", productResponseList));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable @Positive Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(new ApiResponse("Found", productResponseMapper.mapToDto(product)));
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive Long id) {
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
        
        List<ProductResponse> response = foundProducts.stream()
                                                      .map(productResponseMapper::mapToDto)
                                                      .toList();
        
        HttpStatus status = response.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        String message = response.isEmpty() ? "Not found" : "Found";
        
        return new ResponseEntity<>(new ApiResponse(message, response), status);
    }
}
