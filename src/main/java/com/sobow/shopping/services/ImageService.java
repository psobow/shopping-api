package com.sobow.shopping.services;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.dto.FileContent;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
    List<Image> saveImages(List<MultipartFile> files, Long productId);
    
    Image updateById(MultipartFile patch, Long existingId);
    
    Image findById(Long id);
    
    void deleteById(Long id);
    
    FileContent getImageContent(Long id);
}
