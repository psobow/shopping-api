package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.ImageRepository;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.exceptions.ImageProcessingException;
import com.sobow.shopping.services.image.Impl.FileContent;
import com.sobow.shopping.services.image.Impl.ImageServiceImpl;
import com.sobow.shopping.services.product.ProductService;
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
            List<Image> resultList = underTest.saveImages(fixtures.productId(), List.of(file));
            
            // Then
            // Assert: repository looked up the product
            verify(productService).findById(fixtures.productId());
            
            // Assert: returned Image is linked with the loaded Product
            Image resultImage = resultList.get(0);
            assertThat(resultImage.getProduct()).isSameAs(product);
            
            // Assert: bytes were copied from MultipartFile
            byte[] fileBytes = file.getBytes();
            int resultLength = (int) resultImage.getFile().length();
            byte[] resultBytes = resultImage.getFile().getBytes(1, resultLength);
            assertThat(resultLength).isEqualTo(fileBytes.length);
            assertThat(resultBytes).isEqualTo(fileBytes);
            
            // Assert: filename was copied from MultipartFile
            assertThat(resultImage.getFileName()).isEqualTo(file.getOriginalFilename());
            
            // Assert: content type was copied from MultipartFile
            assertThat(resultImage.getFileType()).isEqualTo(file.getContentType());
        }
        
        @Test
        public void saveImages_should_ThrowImageProcessingException_when_GetBytesFails() throws Exception {
            // Given
            Product product = fixtures.productEntity();
            
            when(productService.findById(fixtures.productId())).thenReturn(product);
            
            MultipartFile bad = mock(MultipartFile.class);
            when(bad.getBytes()).thenThrow(new IOException("Boom!"));
            
            // When & Then
            // Assert: throws when MultipartFile#getBytes() fails
            assertThrows(ImageProcessingException.class,
                         () -> underTest.saveImages(fixtures.productId(), List.of(bad)));
            
            // Assert: repository looked up the product
            verify(productService).findById(fixtures.productId());
            
            // Assert: product remains unchanged (no images linked)
            assertThat(product.getImages()).isEmpty();
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
            Image result = underTest.updateByProductIdAndId(fixtures.imageId(), patch);
            
            // Then
            // Assert: repository looked up the entity
            verify(imageRepository).findById(fixtures.imageId());
            
            // Assert: service returns the same managed instance (updated in place)
            assertThat(result).isSameAs(image);
            
            // Assert: bytes were replaced from MultipartFile
            int resultLength = (int) result.getFile().length();
            byte[] resultBytes = result.getFile().getBytes(1, resultLength);
            assertThat(resultBytes).isEqualTo(patch.getBytes());
            
            // Assert: filename was copied from MultipartFile
            assertThat(result.getFileName()).isEqualTo(patch.getOriginalFilename());
            
            // Assert: content type was copied from MultipartFile
            assertThat(result.getFileType()).isEqualTo(patch.getContentType());
        }
        
        @Test
        public void updateById_should_ThrowImageProcessingException_when_GetBytesFails() throws Exception {
            // Given
            Image image = fixtures.imageEntity();
            
            // Snapshots
            String nameBefore = image.getFileName();
            String typeBefore = image.getFileType();
            long lenBefore = image.getFile().length();
            
            when(imageRepository.findById(fixtures.imageId())).thenReturn(Optional.of(image));
            
            MultipartFile badPatch = mock(MultipartFile.class);
            when(badPatch.getBytes()).thenThrow(new IOException("Boom!"));
            
            // When & Then
            // Assert: throws when MultipartFile#getBytes() fails
            assertThrows(ImageProcessingException.class, () -> underTest.updateByProductIdAndId(fixtures.imageId(), badPatch));
            
            // Assert: entity remains unchanged after failure
            assertThat(image.getFileName()).isEqualTo(nameBefore);
            assertThat(image.getFileType()).isEqualTo(typeBefore);
            assertThat(image.getFile().length()).isEqualTo(lenBefore);
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
            // Assert: repository looked up the entity
            verify(imageRepository).findById(fixtures.imageId());
            
            // Assert: metadata copied from Image
            assertThat(result.fileName()).isEqualTo(image.getFileName());
            assertThat(result.fileType()).isEqualTo(image.getFileType());
            assertThat(result.length()).isEqualTo(image.getFile().length());
            
            // Assert: bytes copied from Image BLOB
            byte[] expected = image.getFile().getBytes(1, (int) image.getFile().length());
            assertThat(result.bytes()).isEqualTo(expected);
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
            
            // When & Then
            // Assert: throws ImageProcessingException wrapping the SQLException
            assertThrows(ImageProcessingException.class, () -> underTest.getImageContent(fixtures.imageId()));
        }
    }
}
