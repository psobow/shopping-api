package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Category;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import com.sobow.shopping.exceptions.ProductAlreadyExistsException;
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
    private final Mapper<Product, ProductCreateRequest> productCreateRequestMapper;
    
    @Transactional
    @Override
    public Product create(ProductCreateRequest productCreateRequest) {
        assertProductUnique(productCreateRequest.name(), productCreateRequest.brandName());
        Product product = productCreateRequestMapper.mapToEntity(productCreateRequest);
        Category category = categoryService.findById(productCreateRequest.categoryId());
        category.addProductAndLink(product);
        return product;
    }
    
    @Transactional
    @Override
    public Product partialUpdateById(ProductUpdateRequest patch, long id) {
        
        Product existingProduct = findById(id);
        
        if (patch.name() != null) existingProduct.setName(patch.name());
        if (patch.brandName() != null) existingProduct.setBrandName(patch.brandName());
        
        if (patch.name() != null || patch.brandName() != null) {
            assertProductUnique(existingProduct.getName(), existingProduct.getBrandName());
        }
        
        if (patch.price() != null) existingProduct.setPrice(patch.price());
        if (patch.availableQuantity() != null) existingProduct.setAvailableQuantity(patch.availableQuantity());
        if (patch.description() != null) existingProduct.setDescription(patch.description());
        if (patch.categoryId() != null) {
            Category category = categoryService.findById(patch.categoryId());
            existingProduct.setCategory(category);
        }
        
        return existingProduct;
    }
    
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @Override
    public Product findById(long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
            "Product with id " + id + " not found"));
    }
    
    @Override
    public Product findWithCategoryAndImagesById(long id) {
        return productRepository.findWithCategoryAndImagesById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }
    
    @Override
    public List<Product> findAllWithCategoryAndImages() {
        return productRepository.findAllWithCategoryAndImages();
    }
    
    @Override
    public void deleteById(long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(long id) {
        return productRepository.existsById(id);
    }
    
    
    @Override
    public List<Product> search(String nameLike,
                                String brandName,
                                String categoryName) {
        
        // normalize blanks to nulls
        nameLike = trimToNull(nameLike);
        brandName = trimToNull(brandName);
        categoryName = trimToNull(categoryName);
        
        var spec = Specification.allOf(
            Optional.of(nameLike).map(ProductServiceImpl::nameLike).orElse(null),
            Optional.of(brandName).map(ProductServiceImpl::brandNameEquals).orElse(null),
            Optional.of(categoryName).map(ProductServiceImpl::categoryNameEquals).orElse(null)
        );
        return productRepository.findAll(spec);
    }
    
    private static String trimToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
    
    private static Specification<Product> nameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"
        );
    }
    
    private static Specification<Product> brandNameEquals(String brandName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("brandName"), brandName);
    }
    
    private static Specification<Product> categoryNameEquals(String categoryName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("category").get("name"), categoryName);
    }
    
    private void assertProductUnique(String name, String brandName) {
        if (productRepository.existsByNameAndBrandName(name, brandName)) {
            throw new ProductAlreadyExistsException(name, brandName);
        }
    }
}
