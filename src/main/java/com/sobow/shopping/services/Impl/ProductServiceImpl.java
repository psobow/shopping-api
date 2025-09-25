package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.dto.ProductCreateRequest;
import com.sobow.shopping.domain.product.dto.ProductUpdateRequest;
import com.sobow.shopping.exceptions.ProductAlreadyExistsException;
import com.sobow.shopping.mappers.product.ProductCreateRequestMapper;
import com.sobow.shopping.repositories.ProductRepository;
import com.sobow.shopping.services.CategoryService;
import com.sobow.shopping.services.ProductService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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
    
    private final ProductCreateRequestMapper productCreateRequestMapper;
    
    @Transactional
    @Override
    public Product create(ProductCreateRequest createRequest) {
        assertProductUnique(createRequest.name(), createRequest.brandName(), null);
        Product product = productCreateRequestMapper.mapToEntity(createRequest);
        Category category = categoryService.findById(createRequest.categoryId());
        category.addProductAndLink(product);
        return product;
    }
    
    @Transactional
    @Override
    public Product partialUpdateById(long id, ProductUpdateRequest updateRequest) {
        Product existingProduct = findById(id);
        existingProduct.updateFrom(updateRequest);
        
        // If name or brandName were present in patch, validate if duplication occurs
        if (updateRequest.name() != null || updateRequest.brandName() != null) {
            assertProductUnique(existingProduct.getName(), existingProduct.getBrandName(), existingProduct.getId());
        }
        
        if (updateRequest.categoryId() != null) {
            Category category = categoryService.findById(updateRequest.categoryId());
            existingProduct.linkTo(category);
        }
        
        return existingProduct;
    }
    
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @Transactional(propagation = Propagation.MANDATORY) // throw if there's no existing transaction
    @Override
    public List<Product> lockForOrder(List<Long> ids) {
        List<Long> sorted = ids.stream() // deadlock prevention. Always lock rows in the same order
                               .distinct()
                               .sorted()
                               .toList();
        
        return productRepository.findAllForUpdate(sorted);
    }
    
    @Override
    public Product findById(long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
            "Product with id " + id + " not found"));
    }
    
    @Override
    public Product findWithCategoryAndImagesById(long id) {
        return productRepository.findByIdWithImages(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }
    
    @Override
    public List<Product> findAllWithCategoryAndImages() {
        return productRepository.findAllWithImages();
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
    
    private void assertProductUnique(String name, String brand, @Nullable Long existingProductId) {
        boolean duplicate =
            (existingProductId == null && productRepository.existsByNameAndBrandName(name, brand)) ||
                (existingProductId != null && productRepository.existsByNameAndBrandNameAndIdNot(name, brand, existingProductId));
        
        if (duplicate) {
            throw new ProductAlreadyExistsException(name, brand);
        }
    }
}
