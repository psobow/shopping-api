package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.dto.FileContent;
import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.repositories.ImageRepository;
import com.sobow.shopping.services.Impl.ImageServiceImpl;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.rowset.serial.SerialBlob;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTests {
    
    @Mock
    private ProductService productService;
    
    @Mock
    private ImageRepository imageRepository;
    
    @InjectMocks
    private ImageServiceImpl underTest;
    
    private final static long imageExistingId = 1L;
    private final static long productExistingId = 2L;
    
    @Nested
    @DisplayName("saveImages")
    class saveImages {
        
        @Test
        public void saveImages_should_PersistAndReturnEntities_when_ValidInput() throws Exception {
            // Given
            Product product = new Product();
            MockMultipartFile f1 = new MockMultipartFile(
                "file", "photo.png", "image/png", new byte[]{1, 2, 3});
            
            when(productService.findById(productExistingId)).thenReturn(product);
            
            // When
            List<Image> result = underTest.saveImages(List.of(f1), productExistingId);
            
            // Then
            assertEquals(1, result.size());
            Image resultImage = result.get(0);
            
            byte[] resultBytes = resultImage.getImage().getBytes(1, (int) resultImage.getImage().length());
            assertArrayEquals(f1.getBytes(), resultBytes);
            assertEquals(f1.getOriginalFilename(), resultImage.getFileName());
            assertEquals(f1.getContentType(), resultImage.getFileType());
            assertSame(product, resultImage.getProduct());
        }
        
        @Test
        public void saveImages_should_ThrowImageProcessingException_when_GetBytesFails() throws Exception {
            // Given
            Product product = new Product();
            when(productService.findById(productExistingId)).thenReturn(product);
            
            MultipartFile bad = mock(MultipartFile.class);
            when(bad.getBytes()).thenThrow(new IOException("Boom!"));
            
            // When & Then
            assertThrows(ImageProcessingException.class,
                         () -> underTest.saveImages(List.of(bad), productExistingId));
            verify(imageRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("updateById")
    class updateById {
        
        @Test
        public void updateById_should_ReturnUpdatedImage_when_ValidInput() throws Exception {
            // Given
            Image image = new Image();
            
            MockMultipartFile patch = new MockMultipartFile(
                "file", "photo.png", "image/png", new byte[]{1, 2, 3});
            
            when(imageRepository.findById(imageExistingId)).thenReturn(Optional.of(image));
            when(imageRepository.save(image)).thenReturn(image);
            
            // When
            Image result = underTest.updateById(patch, imageExistingId);
            
            // Then
            byte[] resultBytes = result.getImage().getBytes(1, (int) result.getImage().length());
            assertArrayEquals(patch.getBytes(), resultBytes);
            assertEquals(patch.getOriginalFilename(), result.getFileName());
            assertEquals(patch.getContentType(), result.getFileType());
        }
        
        @Test
        public void updateById_should_ThrowImageProcessingException_when_GetBytesFails() throws Exception {
            // Given
            Image existing = new Image();
            when(imageRepository.findById(imageExistingId)).thenReturn(Optional.of(existing));
            
            MultipartFile bad = mock(MultipartFile.class);
            when(bad.getBytes()).thenThrow(new IOException("Boom!"));
            
            // When & Then
            assertThrows(ImageProcessingException.class, () -> underTest.updateById(bad, imageExistingId));
            verify(imageRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("getImageContent")
    class getImageContent {
        
        @Test
        void getImageContent_should_ReturnFileContent_when_ImageIdValid() throws SQLException {
            // Given
            Image image = new Image();
            image.setFileName("photo.png");
            image.setFileType("image/png");
            byte[] bytes = new byte[]{1, 2, 3};
            image.setImage(new SerialBlob(bytes));
            
            when(imageRepository.findById(imageExistingId)).thenReturn(Optional.of(image));
            
            // When
            FileContent result = underTest.getImageContent(imageExistingId);
            
            // Then
            byte[] resultBytes = result.bytes();
            assertArrayEquals(bytes, resultBytes);
            assertEquals(image.getFileName(), result.fileName());
            assertEquals(image.getFileType(), result.fileType());
        }
        
        @Test
        void getImageContent_should_ThrowImageProcessingException_when_GetBytesFails() throws Exception {
            // Given
            Blob bad = mock(Blob.class);
            long bytesArrayLength = 10L;
            when(bad.length()).thenReturn(bytesArrayLength);
            when(bad.getBytes(1, (int) bytesArrayLength)).thenThrow(new SQLException());
            
            Image image = new Image();
            image.setImage(bad);
            
            when(imageRepository.findById(imageExistingId)).thenReturn(Optional.of(image));
            
            // When + Then
            assertThrows(ImageProcessingException.class, () -> underTest.getImageContent(imageExistingId));
        }
    }
}
