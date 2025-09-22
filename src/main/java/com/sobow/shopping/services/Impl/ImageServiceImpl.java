package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.FileContent;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.repositories.ImageRepository;
import com.sobow.shopping.services.ImageService;
import com.sobow.shopping.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    
    private final ProductService productService;
    private final ImageRepository imageRepository;
    
    @Transactional
    @Override
    public List<Image> saveImages(long productId, List<MultipartFile> files) {
        Product product = productService.findById(productId);
        List<Image> result = new ArrayList<>();
        for (MultipartFile file : files) {
            Image image = new Image();
            image.updateFrom(file);
            product.addImageAndLink(image);
            result.add(image);
        }
        return result;
    }
    
    @Transactional
    @Override
    public Image updateById(long id, MultipartFile patch) {
        Image image = findById(id);
        image.updateFrom(patch);
        return image;
    }
    
    @Override
    public Image findById(long id) {
        return imageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
            "Image with id " + id + " not found"));
    }
    
    @Override
    public void deleteById(long id) {
        imageRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    @Override
    public FileContent getImageContent(long id) {
        Image img = findById(id);
        try {
            return new FileContent(
                img.getFileName(),
                img.getFileType(),
                img.getFile().length(),
                img.getFile().getBytes(1, (int) img.getFile().length())
            );
        } catch (SQLException e) {
            throw new ImageProcessingException(
                "Failed to process image file: " + img.getFileName(), e);
        }
    }
}
