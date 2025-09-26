package com.sobow.shopping.services;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.FileContent;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
    Image findByProductIdAndId(long productId, long imageId);
    
    
    List<Image> saveImages(long productId, List<MultipartFile> files);
    
    Image updateByProductIdAndId(long productId, long imageId, MultipartFile patch);
    
    void deleteByProductIdAndId(long productId, long imageId);
    
    FileContent getImageContent(long productId, long imageId);
}
