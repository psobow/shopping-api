package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.image.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    
    Optional<Image> findByProductIdAndId(long productId, long id);
    
}
