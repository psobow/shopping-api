package com.sobow.shopping.services;

import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.ProductCreateRequest;
import com.sobow.shopping.domain.product.ProductUpdateRequest;
import java.util.List;

public interface ProductService {
    
    Product create(ProductCreateRequest productCreateRequest);
    
    Product findById(long id);
    
    Product findWithCategoryAndImagesById(long id);
    
    List<Product> findAllWithCategoryAndImages();
    
    void deleteById(long id);
    
    boolean existsById(long id);
    
    Product partialUpdateById(ProductUpdateRequest patch, long id);
    
    List<Product> findAll();
    
    List<Product> lockForOrder(List<Long> ids);
    
    void decrementAvailableQty(long id, int decrementQty);
    
    List<Product> search(String nameLike,
                         String brandName,
                         String categoryName);
}
