package com.sobow.shopping.services;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.FileContent;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
    List<Image> saveImages(long productId, List<MultipartFile> files);
    
    Image updateById(long id, MultipartFile patch);
    
    Image findById(long id);
    
    void deleteById(long id);
    
    FileContent getImageContent(long id);
}
