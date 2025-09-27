package com.sobow.shopping.controllers.user;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductResponse;
import com.sobow.shopping.services.ProductService;
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
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<Product> products = productService.findAll();
        List<ProductResponse> responseList = productService.mapProductsToResponsesWithImageIds(products);
        return ResponseEntity.ok(new ApiResponse("Found", responseList));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable @Positive long id) {
        Product product = productService.findById(id);
        List<ProductResponse> responseList = productService.mapProductsToResponsesWithImageIds(List.of(product));
        ProductResponse response = responseList.getFirst();
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String brandName,
        @RequestParam(required = false) String categoryName
    ) {
        List<Product> products = productService.search(name, brandName, categoryName);
        List<ProductResponse> responseList = productService.mapProductsToResponsesWithImageIds(products);
        
        HttpStatus status = responseList.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        String message = responseList.isEmpty() ? "Not found" : "Found";
        
        return new ResponseEntity<>(new ApiResponse(message, responseList), status);
    }
}
