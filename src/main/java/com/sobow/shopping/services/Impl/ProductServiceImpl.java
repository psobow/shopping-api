package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.dto.ProductCreateRequest;
import com.sobow.shopping.domain.dto.ProductUpdateRequest;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.mappers.ProductMapper;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Mapper<Product, ProductCreateRequest> productMapper;
    
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }
    
    @Override
    public Product save(ProductCreateRequest productCreateRequest) {
        Product product = productMapper.mapToEntity(productCreateRequest);
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
    public Product partialUpdateById(ProductUpdateRequest productUpdateRequest, Long id) {
        
        Product existingProduct = productRepository.findById(id)
                                                   .orElseThrow(() -> new EntityNotFoundException(
                                                       "Product with id " + id + " not found"));
        
        // simple null checks for other fields
        if (productUpdateRequest.name() != null) existingProduct.setName(productUpdateRequest.name());
        if (productUpdateRequest.brandName() != null) existingProduct.setBrandName(productUpdateRequest.brandName());
        if (productUpdateRequest.price() != null) existingProduct.setPrice(productUpdateRequest.price());
        if (productUpdateRequest.availableQuantity() != null) {
            existingProduct.setAvailableQuantity(productUpdateRequest.availableQuantity());
        }
        if (productUpdateRequest.description() != null) {
            existingProduct.setDescription(productUpdateRequest.description());
        }
        
        // handle category update safely
        if (productUpdateRequest.categoryId() != null) {
            Category category = categoryRepository.findById(productUpdateRequest.categoryId())
                                                  .orElseThrow(() -> new EntityNotFoundException(
                                                      "Category with id " + productUpdateRequest.categoryId()
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
