package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.repositories.ImageRepository;
import com.sobow.shopping.services.Impl.ImageServiceImpl;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.sql.rowset.serial.SerialBlob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTests {
    
    @Mock
    private ProductService productService;
    
    @Mock
    private ImageRepository imageRepository;
    
    @InjectMocks
    private ImageServiceImpl underTest;
    
    @Test
    public void saveImages_saveFiles_and_returnImages() throws Exception {
        // Given
        Long productId = 10L;
        Product product = new Product();
        when(productService.findById(productId)).thenReturn(product);
        
        MultipartFile f1 = mock(MultipartFile.class);
        
        byte[] photoBytes = new byte[]{1, 2, 3};
        when(f1.getBytes()).thenReturn(photoBytes);
        when(f1.getOriginalFilename()).thenReturn("photo1.png");
        when(f1.getContentType()).thenReturn("image/png");
        
        // When
        List<Image> result = underTest.saveImages(List.of(f1), productId);
        
        // Then
        assertEquals(1, result.size());
        Image resultImage = result.get(0);
        
        byte[] resultBytes = resultImage.getImage().getBytes(1, (int) resultImage.getImage().length());
        assertArrayEquals(photoBytes, resultBytes);
        assertEquals("photo1.png", resultImage.getFileName());
        assertEquals("image/png", resultImage.getFileType());
        assertSame(product, resultImage.getProduct());
        
        verify(productService).findById(productId);
        verify(imageRepository).save(any());
        verifyNoMoreInteractions(imageRepository);
    }
    
    @Test
    public void saveImages_wrapsBytesError() throws Exception {
        // Given
        Long productId = 10L;
        Product product = new Product();
        when(productService.findById(productId)).thenReturn(product);
        
        MultipartFile bad = mock(MultipartFile.class);
        when(bad.getOriginalFilename()).thenReturn("broken.png");
        when(bad.getBytes()).thenThrow(new IOException("boom"));
        
        // When & Then
        ImageProcessingException exception =
            assertThrows(ImageProcessingException.class,
                         () -> underTest.saveImages(List.of(bad), productId));
        assertTrue(exception.getMessage().contains("broken.png"));
        verify(imageRepository, never()).save(any());
    }
    
    @Test
    public void updateById_updateFile_and_returnFile() throws Exception {
        
        // Given
        Long existingId = 10L;
        Image existing = new Image();
        existing.setImage(new SerialBlob(new byte[]{1, 2, 3}));
        existing.setFileName("old.png");
        when(imageRepository.findById(existingId)).thenReturn(Optional.of(existing));
        
        MultipartFile patch = mock(MultipartFile.class);
        when(patch.getOriginalFilename()).thenReturn("new.png");
        when(patch.getContentType()).thenReturn("image/png");
        byte[] patchBytes = new byte[]{4, 5, 6};
        when(patch.getBytes()).thenReturn(patchBytes);
        when(imageRepository.save(existing)).thenReturn(existing);
        
        // When
        Image result = underTest.updateById(patch, existingId);
        
        // Then
        byte[] resultBytes = result.getImage().getBytes(1, (int) result.getImage().length());
        assertArrayEquals(patchBytes, resultBytes);
        assertEquals("new.png", result.getFileName());
        assertEquals("image/png", result.getFileType());
    }
    
    @Test
    public void updateById_wrapsBytesError() throws Exception {
        // Given
        Long existingId = 10L;
        Image existing = new Image();
        when(imageRepository.findById(existingId)).thenReturn(Optional.of(existing));
        
        MultipartFile bad = mock(MultipartFile.class);
        when(bad.getOriginalFilename()).thenReturn("bad.png");
        when(bad.getBytes()).thenThrow(new IOException("boom"));
        
        // When & Then
        ImageProcessingException exception =
            assertThrows(ImageProcessingException.class, () -> underTest.updateById(bad, existingId));
        verify(imageRepository, never()).save(any());
    }
}
