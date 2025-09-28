package com.sobow.shopping.controllers.product;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.product.dto.ProductCreateRequest;
import com.sobow.shopping.controllers.product.dto.ProductResponse;
import com.sobow.shopping.controllers.product.dto.ProductUpdateRequest;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.mappers.product.ProductResponseMapper;
import com.sobow.shopping.services.product.ProductService;
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
public class ProductManagementController {
    
    private final ProductService productService;
    private final ProductResponseMapper productResponseMapper;
    
    @PostMapping
    public ResponseEntity<ApiResponseDto> createProduct(
        @RequestBody @Valid ProductCreateRequest request
    ) {
        Product saved = productService.create(request);
        ProductResponse response = productResponseMapper.mapToDto(saved);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
                             .body(new ApiResponseDto("Created", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto> updateProduct(
        @RequestBody @Valid ProductUpdateRequest request,
        @PathVariable @Positive long id
    ) {
        Product updated = productService.partialUpdateById(id, request);
        ProductResponse response = productResponseMapper.mapToDto(updated);
        return ResponseEntity.ok(new ApiResponseDto("Updated", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
