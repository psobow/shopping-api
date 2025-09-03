package com.sobow.shopping.services;

import com.sobow.shopping.domain.Image;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
    List<Image> saveImages(List<MultipartFile> files, Long productId);
    
    Image findById(Long id);
    
    void deleteById(Long id);
    
    void updateById(MultipartFile file, Long id);
}
