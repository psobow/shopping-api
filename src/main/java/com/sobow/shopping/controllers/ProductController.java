package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.responses.ApiResponse;
import com.sobow.shopping.domain.responses.ProductResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ProductService;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    
    private final ProductService productService;
    private final Mapper<Product, ProductResponse> productResponseMapper;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<ProductResponse> productResponseList = productService.findAll()
                                                                  .stream()
                                                                  .map(productResponseMapper::mapToDto)
                                                                  .toList();
        
        return ResponseEntity.ok(new ApiResponse("Found", productResponseList));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@Min(1) Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(new ApiResponse("Found", productResponseMapper.mapToDto(product)));
    }
}
