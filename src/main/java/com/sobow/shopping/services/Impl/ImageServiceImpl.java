package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.repositories.ImageRepository;
import com.sobow.shopping.services.ImageService;
import com.sobow.shopping.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {
    
    private final ProductService productService;
    private final ImageRepository imageRepository;
    
    public ImageServiceImpl(ProductService productService, ImageRepository imageRepository) {
        this.productService = productService;
        this.imageRepository = imageRepository;
    }
    
    @Transactional
    @Override
    public List<Image> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.findById(productId);
        List<Image> images = new ArrayList<>();
        
        for (MultipartFile file : files) {
            Image image = new Image();
            try {
                image.setImage(new SerialBlob(file.getBytes()));
            } catch (Exception e) {
                throw new ImageProcessingException("Failed to process image file: " + file.getOriginalFilename(), e);
            }
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setProduct(product);
            imageRepository.save(image);
            
            images.add(image);
        }
        return images;
    }
    
    @Transactional
    @Override
    public Image updateById(MultipartFile patch, Long existingId) {
        Image image = findById(existingId);
        image.setFileName(patch.getOriginalFilename());
        image.setFileType(patch.getContentType());
        try {
            image.setImage(new SerialBlob(patch.getBytes()));
        } catch (Exception e) {
            throw new ImageProcessingException("Failed to process image file: " + patch.getOriginalFilename(), e);
        }
        return imageRepository.save(image);
    }
    
    @Override
    public Image findById(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
            "Image with id " + id + " not found"));
    }
    
    @Override
    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }
}
