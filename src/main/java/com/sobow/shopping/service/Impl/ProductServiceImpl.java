package com.sobow.shopping.service.Impl;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.dto.ProductRequest;
import com.sobow.shopping.repository.CategoryRepository;
import com.sobow.shopping.repository.ProductRepository;
import com.sobow.shopping.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Product save(ProductRequest productRequest) {
        Category category =
            categoryRepository.findById(productRequest.categoryId()).orElseThrow(() -> new EntityNotFoundException(
                "Category with id " + productRequest.categoryId() + " not found"));
        
        Product product = new Product().builder()
                                       .name(productRequest.name())
                                       .brandName(productRequest.brandName())
                                       .price(productRequest.price())
                                       .availableQuantity(productRequest.availableQuantity())
                                       .description(productRequest.description())
                                       .category(category)
                                       .build();
        
        return productRepository.save(product);
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    
    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }
    
    @Override
    public Product partialUpdateById(ProductRequest productRequest, Long id) {
        
        Product existingProduct = productRepository.findById(id)
                                                   .orElseThrow(() -> new EntityNotFoundException(
                                                       "Product with id " + id + " not found"));
        
        // simple null checks for other fields
        if (productRequest.name() != null) existingProduct.setName(productRequest.name());
        if (productRequest.brandName() != null) existingProduct.setBrandName(productRequest.brandName());
        if (productRequest.price() != null) existingProduct.setPrice(productRequest.price());
        if (productRequest.availableQuantity() != null) {
            existingProduct.setAvailableQuantity(productRequest.availableQuantity());
        }
        if (productRequest.description() != null) existingProduct.setDescription(productRequest.description());
        
        // handle category update safely
        if (productRequest.categoryId() != null) {
            Category category = categoryRepository.findById(productRequest.categoryId())
                                                  .orElseThrow(() -> new EntityNotFoundException(
                                                      "Category with id " + productRequest.categoryId()
                                                          + " not found"));
            existingProduct.setCategory(category);
        }
        
        return productRepository.save(existingProduct);
    }
    
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @Override
    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }
    
    @Override
    public List<Product> findByCategory_Name(String categoryName) {
        return productRepository.findByCategory_Name(categoryName);
    }
    
    @Override
    public List<Product> findByCategory_NameAndBrandName(String categoryName, String brandName) {
        return productRepository.findByCategory_NameAndBrandName(categoryName, brandName);
    }
    
    @Override
    public List<Product> findByBrandName(String brandName) {
        return productRepository.findByBrandName(brandName);
    }
    
    @Override
    public List<Product> findByBrandNameAndName(String brandName, String name) {
        return productRepository.findByBrandNameAndName(brandName, name);
    }
    
    @Override
    public Long countByBrandNameAndName(String brandName, String name) {
        return productRepository.countByBrandNameAndName(brandName, name);
    }
}
