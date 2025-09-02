package com.sobow.shopping.service.Impl;

import com.sobow.shopping.domain.Product;
import com.sobow.shopping.repository.ProductRepository;
import com.sobow.shopping.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Product save(Product product) {
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
    public Product partialUpdateById(Product product, Long id) {
        Product existingProduct = productRepository.findById(id)
                                                   .orElseThrow(() -> new EntityNotFoundException(
                                                       "Product with id " + id + " not found"));
        
        if (product.getName() != null) existingProduct.setName(product.getName());
        if (product.getBrandName() != null) existingProduct.setBrandName(product.getBrandName());
        if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
        if (product.getQuantity() != null) existingProduct.setQuantity(product.getQuantity());
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
