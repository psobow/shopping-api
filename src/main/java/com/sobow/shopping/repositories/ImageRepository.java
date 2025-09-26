package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.ProductIdImageId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    
    Optional<Image> findByProductIdAndId(long productId, long imageId);
    
    @Query("""
           SELECT i.product.id AS productId, i.id AS imageId
           FROM Image i
           WHERE i.product.id in :productIds
        """)
    List<ProductIdImageId> findImageIdsByProductIds(List<Long> productIds);
}
