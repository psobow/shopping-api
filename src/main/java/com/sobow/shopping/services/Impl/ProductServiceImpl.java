package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.mappers.ProductMapper;
import com.sobow.shopping.repositories.CategoryRepository;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.CategoryService;
import com.sobow.shopping.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;

    
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CategoryService categoryService, ProductMapper productMapper) {
        this.productRepository = productRepository;
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
    
    @Transactional
    @Override
    public Product partialUpdateById(Product patch, Long existingId) {
        
        Product existingProduct = findById(existingId);
        
        if (patch.getName() != null) existingProduct.setName(patch.getName());
        if (patch.getBrandName() != null) existingProduct.setBrandName(patch.getBrandName());
        if (patch.getPrice() != null) existingProduct.setPrice(patch.getPrice());
        if (patch.getAvailableQuantity() != null) existingProduct.setAvailableQuantity(patch.getAvailableQuantity());
        if (patch.getDescription() != null) existingProduct.setDescription(patch.getDescription());
        if (patch.getCategory() != null) existingProduct.setCategory(patch.getCategory());
        
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
