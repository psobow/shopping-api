package com.sobow.shopping.services;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.dto.FileContent;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
    List<Image> saveImages(List<MultipartFile> files, long productId);
    
    Image updateById(MultipartFile patch, long id);
    
    Image findById(long id);
    
    void deleteById(long id);
    
    FileContent getImageContent(long id);
}
