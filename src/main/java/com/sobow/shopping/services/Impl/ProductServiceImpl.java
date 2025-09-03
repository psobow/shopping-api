package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.mappers.ProductMapper;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.CategoryService;
import com.sobow.shopping.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final Mapper<com.sobow.shopping.domain.Product, ProductCreateRequest> productMapper;
    
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CategoryService categoryService, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }
    
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
            "Product with id " + id + " not found"));
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
    public Product partialUpdateById(Product product, Long id) {
        
        Product existingProduct = findById(id);
        
        if (product.getName() != null) existingProduct.setName(product.getName());
        if (product.getBrandName() != null) existingProduct.setBrandName(product.getBrandName());
        if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
        if (product.getAvailableQuantity() != null) existingProduct.setAvailableQuantity(product.getAvailableQuantity());
        if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
        if (product.getCategory() != null) existingProduct.setCategory(product.getCategory());
        
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
