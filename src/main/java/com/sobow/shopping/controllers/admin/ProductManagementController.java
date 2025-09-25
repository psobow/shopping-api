package com.sobow.shopping.controllers.admin;

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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class ProductManagementController {
    
    private final ProductService productService;
    @Qualifier("productResponseMapper")
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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
