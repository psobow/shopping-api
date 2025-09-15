package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.Product;
import com.sobow.shopping.domain.dto.FileContent;
import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.repositories.ImageRepository;
import com.sobow.shopping.services.Impl.ImageServiceImpl;
import com.sobow.shopping.utils.TestFixtures;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("saveImages")
    class saveImages {
        
        @Test
        public void saveImages_should_PersistAndReturnEntities_when_ValidInput() throws Exception {
            // Given
            Product product = fixtures.productEntity();
            MockMultipartFile file = fixtures.multipartFile();
            
            when(productService.findById(fixtures.productId())).thenReturn(product);
            
            // When
            List<Image> resultList = underTest.saveImages(List.of(file), fixtures.productId());
            
            // Then
            Image resultImage = resultList.get(0);
            assertSame(product, resultImage.getProduct());
            
            byte[] resultBytes = resultImage.getFile().getBytes(1, (int) resultImage.getFile().length());
            assertArrayEquals(file.getBytes(), resultBytes);
            assertEquals(file.getOriginalFilename(), resultImage.getFileName());
            assertEquals(file.getContentType(), resultImage.getFileType());
        }
        
        @Test
        public void saveImages_should_ThrowImageProcessingException_when_GetBytesFails() throws Exception {
            // Given
            Product product = fixtures.productEntity();
            
            when(productService.findById(fixtures.productId())).thenReturn(product);
            
            MultipartFile bad = mock(MultipartFile.class);
            when(bad.getBytes()).thenThrow(new IOException("Boom!"));
            
            // When & Then
            assertThrows(ImageProcessingException.class,
                         () -> underTest.saveImages(List.of(bad), fixtures.productId()));
        }
    }
    
    @Nested
    @DisplayName("updateById")
    class updateById {
        
        @Test
        public void updateById_should_ReturnUpdatedImage_when_ValidInput() throws Exception {
            // Given
            Image image = fixtures.imageEntity();
            
            MockMultipartFile patch = fixtures.withMultipartByteArray(new byte[]{1, 2, 3})
                                              .multipartFile();
            
            when(imageRepository.findById(fixtures.imageId())).thenReturn(Optional.of(image));
            
            // When
            Image result = underTest.updateById(patch, fixtures.imageId());
            
            // Then
            assertSame(image, result);
            
            byte[] resultBytes = result.getFile().getBytes(1, (int) result.getFile().length());
            assertArrayEquals(patch.getBytes(), resultBytes);
            assertEquals(patch.getOriginalFilename(), result.getFileName());
            assertEquals(patch.getContentType(), result.getFileType());
        }
        
        @Test
        public void updateById_should_ThrowImageProcessingException_when_GetBytesFails() throws Exception {
            // Given
            Image image = fixtures.imageEntity();
            when(imageRepository.findById(fixtures.imageId())).thenReturn(Optional.of(image));
            
            MultipartFile badPatch = mock(MultipartFile.class);
            when(badPatch.getBytes()).thenThrow(new IOException("Boom!"));
            
            // When & Then
            assertThrows(ImageProcessingException.class, () -> underTest.updateById(badPatch, fixtures.imageId()));
        }
    }
    
    @Nested
    @DisplayName("getImageContent")
    class getImageContent {
        
        @Test
        void getImageContent_should_ReturnFileContent_when_ImageIdValid() throws SQLException {
            // Given
            Image image = fixtures.imageEntity();
            
            when(imageRepository.findById(fixtures.imageId())).thenReturn(Optional.of(image));
            
            // When
            FileContent result = underTest.getImageContent(fixtures.imageId());
            
            // Then
            byte[] resultBytes = result.bytes();
            byte[] imageBytes = image.getFile().getBytes(1, (int) image.getFile().length());
            assertArrayEquals(imageBytes, resultBytes);
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
            
            Image image = fixtures.withImageFile(bad)
                                  .imageEntity();
            
            when(imageRepository.findById(fixtures.imageId())).thenReturn(Optional.of(image));
            
            // When + Then
            assertThrows(ImageProcessingException.class, () -> underTest.getImageContent(fixtures.imageId()));
        }
    }
}
