package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.ProductCreateRequest;
import com.sobow.shopping.domain.product.ProductUpdateRequest;
import com.sobow.shopping.exceptions.ProductAlreadyExistsException;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.CategoryService;
import com.sobow.shopping.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final Mapper<Product, ProductCreateRequest> productCreateRequestMapper;
    
    @Transactional
    @Override
    public Product create(ProductCreateRequest request) {
        String name = normalize(request.name());
        String brand = normalize(request.brandName());
        assertProductUnique(name, brand);
        
        Product product = productCreateRequestMapper.mapToEntity(request);
        product.setName(name);
        product.setBrandName(brand);
        
        Category category = categoryService.findById(request.categoryId());
        category.addProductAndLink(product);
        return product;
    }
    
    @Transactional
    @Override
    public Product partialUpdateById(ProductUpdateRequest patch, long id) {
        Product existingProduct = findById(id);
        
        if (patch.name() != null) existingProduct.setName(normalize(patch.name()));
        if (patch.brandName() != null) existingProduct.setBrandName(normalize(patch.brandName()));
        
        // If name or brandName were present in patch, validate if duplication occurs
        if (patch.name() != null || patch.brandName() != null) {
            assertProductUnique(existingProduct.getName(), existingProduct.getBrandName());
        }
        
        if (patch.price() != null) existingProduct.setPrice(patch.price());
        if (patch.availableQuantity() != null) existingProduct.setAvailableQty(patch.availableQuantity());
        if (patch.description() != null) existingProduct.setDescription(patch.description());
        if (patch.categoryId() != null) {
            Category category = categoryService.findById(patch.categoryId());
            existingProduct.linkTo(category);
        }
        
        return existingProduct;
    }
    
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public List<Product> lockForOrder(List<Long> ids) {
        List<Long> sorted = ids.stream() // deadlock prevention, always lock rows in the same order
                               .distinct()
                               .sorted()
                               .toList();
        
        return productRepository.findAllForUpdate(sorted);
    }
    
    @Override
    public void decrementAvailableQty(long id, int decrementQty) {
        Product p = findById(id);
        int availableQty = p.getAvailableQty();
        int newQty = availableQty - decrementQty;
        p.setAvailableQty(newQty);
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
        nameLike = stripToNull(nameLike);
        brandName = stripToNull(brandName);
        categoryName = stripToNull(categoryName);
        
        var spec = Specification.allOf(
            Optional.of(nameLike).map(ProductServiceImpl::nameLike).orElse(null),
            Optional.of(brandName).map(ProductServiceImpl::brandNameEquals).orElse(null),
            Optional.of(categoryName).map(ProductServiceImpl::categoryNameEquals).orElse(null)
        );
        return productRepository.findAll(spec);
    }
    
    private static String stripToNull(String s) {
        return (s == null || s.strip().isEmpty()) ? null : s.strip();
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
    
    private String normalize(String s) {
        return Objects.requireNonNull(s, "value required").strip().toLowerCase(Locale.ROOT);
    }
}
