package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductRequest;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.CategoryService;
import com.sobow.shopping.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final Mapper<Product, ProductRequest> productRequestMapper;
    
    private static String trimToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
    
    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
            "Product with id " + id + " not found"));
    }
    
    @Override
    public Product findProductWithCategoryAndImagesById(Long id) {
        return productRepository.findProductWithCategoryAndImagesById(id).orElseThrow(() -> new EntityNotFoundException(
            "Product with id " + id + " not found"));
    }
    
    private static Specification<Product> nameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"
        );
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
    public Product partialUpdateById(ProductRequest patch, Long id) {
        
        Product existingProduct = findById(id);
        
        if (patch.name() != null) existingProduct.setName(patch.name());
        if (patch.brandName() != null) existingProduct.setBrandName(patch.brandName());
        if (patch.price() != null) existingProduct.setPrice(patch.price());
        if (patch.availableQuantity() != null) existingProduct.setAvailableQuantity(patch.availableQuantity());
        if (patch.description() != null) existingProduct.setDescription(patch.description());
        if (patch.categoryId() != null) {
            Category category = categoryService.findById(patch.categoryId());
            existingProduct.setCategory(category);
        }
        
        return productRepository.save(existingProduct);
    }
    
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @Override
    public List<Product> search(String name,
                                String brandName,
                                String categoryName) {
        
        // normalize blanks to nulls
        name = trimToNull(name);
        brandName = trimToNull(brandName);
        categoryName = trimToNull(categoryName);
        
        var spec = Specification.allOf(
            Optional.of(name).map(ProductServiceImpl::nameLike).orElse(null),
            Optional.of(brandName).map(ProductServiceImpl::brandNameEquals).orElse(null),
            Optional.of(categoryName).map(ProductServiceImpl::categoryNameEquals).orElse(null)
        );
        return productRepository.findAll(spec);
    }
    
    private static Specification<Product> brandNameEquals(String brandName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("brandName"), brandName);
    }
    
    private static Specification<Product> categoryNameEquals(String categoryName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("category").get("name"), categoryName);
    }
    
    @Transactional
    @Override
    public Product save(ProductRequest productRequest) {
        Category category = categoryService.findById(productRequest.categoryId());
        Product product = productRequestMapper.mapToEntity(productRequest);
        category.addProductAndLink(product);
        return productRepository.save(product);
    }
    
    @Override
    public List<Product> findAllProductsWithCategoryAndImages() {
        return productRepository.findAllProductsWithCategoryAndImages();
    }
}
