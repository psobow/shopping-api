package com.sobow.shopping.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sobow.shopping.controllers.admin.ImageManagementController;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.FileContent;
import com.sobow.shopping.domain.image.dto.ImageResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ImageService;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@WebMvcTest(ImageManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ImageManagementControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ImageService imageService;
    
    @MockitoBean
    private Mapper<Image, ImageResponse> imageMapper;
    
    private final static String PRODUCT_IMAGES_BY_PRODUCT_ID_PATH = "/api/products/{productId}/images";
    private final static String IMAGES_BY_ID_PATH = "/api/images/{id}";
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("saveImages")
    class saveImages {
        
        @Test
        public void saveImages_should_Return201WithList_when_ValidRequest() throws Exception {
            // Given
            MockMultipartFile file = fixtures.multipartFile();
            List<Image> saved = List.of(fixtures.imageEntity());
            ImageResponse response = fixtures.imageResponse();
            
            when(imageService.saveImages(fixtures.productId(), List.of(file))).thenReturn(saved);
            when(imageMapper.mapToDto(saved.get(0))).thenReturn(response);
            
            // When & Then
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.productId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isCreated())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.message").value("Upload success"))
                   .andExpect(jsonPath("$.data[0].id").value(fixtures.imageId()))
                   .andExpect(jsonPath("$.data[0].fileName").value(response.fileName()))
                   .andExpect(jsonPath("$.data[0].downloadUrl").value(response.downloadUrl()));
        }
        
        @Test
        public void saveImages_should_Return400_when_FilePartMissing() throws Exception {
            // When & Then
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.productId())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).saveImages(anyLong(), anyList());
        }
        
        @Test
        public void saveImages_should_Return400_when_PartNameIsWrong() throws Exception {
            // Given
            MockMultipartFile badFile = new MockMultipartFile(
                "wrongName_shouldBe_file",
                "photo.png",
                "image/png",
                new byte[]{1}
            );
            
            // When & Then
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.productId())
                                .file(badFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).saveImages(anyLong(), anyList());
        }
        
        @Test
        public void saveImages_should_Return400_when_ProductIdLessThanOne() throws Exception {
            // Given
            MockMultipartFile file = fixtures.multipartFile();
            
            // When & Then
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.invalidId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).saveImages(anyLong(), anyList());
        }
        
        @Test
        public void saveImages_should_Return404_when_ProductIdDoesNotExist() throws Exception {
            // Given
            MockMultipartFile file = fixtures.multipartFile();
            
            when(imageService.saveImages(fixtures.nonExistingId(), List.of(file)))
                .thenThrow(new EntityNotFoundException());
            
            // When & Then
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.nonExistingId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void saveImages_should_Return415_when_ContentTypeUnsupported() throws Exception {
            // When & Then
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.productId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                   .andExpect(status().isUnsupportedMediaType());
            
            verify(imageService, never()).saveImages(anyLong(), anyList());
        }
        
        @Test
        public void saveImages_should_Return413_when_FileExceedsLimit() throws Exception {
            // Given
            MockMultipartFile file = fixtures.multipartFile();
            
            when(imageService.saveImages(fixtures.productId(), List.of(file)))
                .thenThrow(new MaxUploadSizeExceededException(DataSize.ofMegabytes(5).toBytes()));
            
            // When & Then
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.productId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isPayloadTooLarge())
                   .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
        }
    }
    
    @Nested
    @DisplayName("updateImage")
    class updateImage {
        
        @Test
        public void updateImage_should_Return200WithDto_when_ValidRequest() throws Exception {
            // Given
            MockMultipartFile file = fixtures.withMultipartByteArray(new byte[]{1, 2, 3})
                                             .multipartFile();
            
            Image updated = fixtures.imageEntity();
            ImageResponse response = fixtures.imageResponse();
            
            when(imageService.updateById(fixtures.imageId(), file)).thenReturn(updated);
            when(imageMapper.mapToDto(updated)).thenReturn(response);
            
            // When & Then
            mockMvc.perform(multipart(HttpMethod.PUT, IMAGES_BY_ID_PATH, fixtures.imageId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.message").value("Updated"))
                   .andExpect(jsonPath("$.data.id").value(response.id()))
                   .andExpect(jsonPath("$.data.fileName").value(response.fileName()))
                   .andExpect(jsonPath("$.data.downloadUrl").value(response.downloadUrl()));
        }
        
        @Test
        public void updateImage_should_Return400_when_FilePartMissing() throws Exception {
            // When & Then
            mockMvc.perform(multipart(HttpMethod.PUT, IMAGES_BY_ID_PATH, fixtures.imageId())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).updateById(anyLong(), any());
        }
        
        @Test
        public void updateImage_should_Return400_when_ImageIdLessThanOne() throws Exception {
            // Given
            MockMultipartFile file = fixtures.multipartFile();
            
            // When & Then
            mockMvc.perform(multipart(HttpMethod.PUT, IMAGES_BY_ID_PATH, fixtures.invalidId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).updateById(anyLong(), any());
        }
        
        @Test
        public void updateImage_should_Return415_when_ContentTypeUnsupported() throws Exception {
            // When & Then
            mockMvc.perform(put(IMAGES_BY_ID_PATH, fixtures.imageId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{}"))
                   .andExpect(status().isUnsupportedMediaType());
            
            verify(imageService, never()).updateById(anyLong(), any());
        }
        
        @Test
        public void updateImage_should_Return404_when_ImageIdDoesNotExist() throws Exception {
            // Given
            MockMultipartFile file = fixtures.multipartFile();
            when(imageService.updateById(fixtures.nonExistingId(), file))
                .thenThrow(new EntityNotFoundException());
            
            // When & Then
            mockMvc.perform(multipart(HttpMethod.PUT, IMAGES_BY_ID_PATH, fixtures.nonExistingId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("downloadImage")
    class downloadImage {
        
        @Test
        public void downloadImage_should_Return200WithFile_when_ImageIdValid() throws Exception {
            // Given
            FileContent fileContent = fixtures.fileContent();
            
            when(imageService.getImageContent(fixtures.imageId())).thenReturn(fileContent);
            
            // When & Then
            mockMvc.perform(get(IMAGES_BY_ID_PATH, fixtures.imageId()))
                   .andExpect(status().isOk())
                   .andExpect(header().string("Content-Type", fileContent.fileType()))
                   .andExpect(header().string("Content-Length", String.valueOf(fileContent.length())))
                   .andExpect(header().string("Content-Disposition", String.format(
                       "attachment; filename=\"%s\"", fileContent.fileName())))
                   .andExpect(content().bytes(fileContent.bytes()));
        }
        
        @Test
        public void downloadImage_should_Return400_when_ImageIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(get(IMAGES_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).getImageContent(anyLong());
        }
        
        @Test
        public void downloadImage_should_Return404_when_ImageIdDoesNotExist() throws Exception {
            // Given
            when(imageService.getImageContent(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            // When & Then
            mockMvc.perform(get(IMAGES_BY_ID_PATH, fixtures.nonExistingId()))
                   .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("deleteImage")
    class deleteImage {
        
        @Test
        public void deleteImage_should_Return204_when_Deleted() throws Exception {
            // When & Then
            mockMvc.perform(delete(IMAGES_BY_ID_PATH, fixtures.imageId()))
                   .andExpect(status().isNoContent());
            verify(imageService).deleteById(fixtures.imageId());
        }
        
        @Test
        public void deleteImage_should_Return400_when_ImageIdLessThanOne() throws Exception {
            // When & Then
            mockMvc.perform(delete(IMAGES_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
            verify(imageService, never()).deleteById(fixtures.invalidId());
        }
    }
}
