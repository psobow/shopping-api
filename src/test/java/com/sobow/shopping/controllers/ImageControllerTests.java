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

import com.sobow.shopping.domain.image.FileContent;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.ImageResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ImageService;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@WebMvcTest(ImageController.class)
public class ImageControllerTests {
    
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
            MockMultipartFile file = fixtures.multipartFile();
            List<Image> saved = List.of(fixtures.imageEntity());
            ImageResponse response = fixtures.imageResponse();
            
            when(imageService.saveImages(List.of(file), fixtures.productId())).thenReturn(saved);
            when(imageMapper.mapToDto(saved.get(0))).thenReturn(response);
            
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
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.productId())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).saveImages(anyList(), anyLong());
        }
        
        @Test
        public void saveImages_should_Return400_when_PartNameIsWrong() throws Exception {
            MockMultipartFile badFile = new MockMultipartFile(
                "wrongName_shouldBe_file",
                "photo.png",
                "image/png",
                new byte[]{1}
            );
            
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.productId())
                                .file(badFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).saveImages(anyList(), anyLong());
        }
        
        @Test
        public void saveImages_should_Return400_when_ProductIdLessThanOne() throws Exception {
            MockMultipartFile file = fixtures.multipartFile();
            
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.invalidId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).saveImages(anyList(), anyLong());
        }
        
        @Test
        public void saveImages_should_Return404_when_ProductIdDoesNotExist() throws Exception {
            MockMultipartFile file = fixtures.multipartFile();
            
            when(imageService.saveImages(List.of(file), fixtures.nonExistingId()))
                .thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.nonExistingId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void saveImages_should_Return415_when_ContentTypeUnsupported() throws Exception {
            mockMvc.perform(multipart(PRODUCT_IMAGES_BY_PRODUCT_ID_PATH, fixtures.productId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                   .andExpect(status().isUnsupportedMediaType());
            
            verify(imageService, never()).saveImages(anyList(), anyLong());
        }
        
        @Test
        public void saveImages_should_Return413_when_FileExceedsLimit() throws Exception {
            MockMultipartFile file = fixtures.multipartFile();
            
            when(imageService.saveImages(List.of(file), fixtures.productId()))
                .thenThrow(new MaxUploadSizeExceededException(DataSize.ofMegabytes(5).toBytes()));
            
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
            MockMultipartFile file = fixtures.withMultipartByteArray(new byte[]{1, 2, 3})
                                             .multipartFile();
            
            Image updated = fixtures.imageEntity();
            ImageResponse response = fixtures.imageResponse();
            
            when(imageService.updateById(file, fixtures.imageId())).thenReturn(updated);
            when(imageMapper.mapToDto(updated)).thenReturn(response);
            
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
            mockMvc.perform(multipart(HttpMethod.PUT, IMAGES_BY_ID_PATH, fixtures.imageId())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).updateById(any(), anyLong());
        }
        
        @Test
        public void updateImage_should_Return400_when_ImageIdLessThanOne() throws Exception {
            MockMultipartFile file = fixtures.multipartFile();
            mockMvc.perform(multipart(HttpMethod.PUT, IMAGES_BY_ID_PATH, fixtures.invalidId())
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).updateById(any(), anyLong());
        }
        
        @Test
        public void updateImage_should_Return415_when_ContentTypeUnsupported() throws Exception {
            mockMvc.perform(put(IMAGES_BY_ID_PATH, fixtures.imageId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{}"))
                   .andExpect(status().isUnsupportedMediaType());
            
            verify(imageService, never()).updateById(any(), anyLong());
        }
        
        @Test
        public void updateImage_should_Return404_when_ImageIdDoesNotExist() throws Exception {
            MockMultipartFile file = fixtures.multipartFile();
            when(imageService.updateById(file, fixtures.nonExistingId()))
                .thenThrow(new EntityNotFoundException());
            
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
            FileContent fileContent = fixtures.fileContent();
            
            when(imageService.getImageContent(fixtures.imageId())).thenReturn(fileContent);
            
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
            mockMvc.perform(get(IMAGES_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).getImageContent(anyLong());
        }
        
        @Test
        public void downloadImage_should_Return404_when_ImageIdDoesNotExist() throws Exception {
            when(imageService.getImageContent(fixtures.nonExistingId())).thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(get(IMAGES_BY_ID_PATH, fixtures.nonExistingId()))
                   .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("deleteImage")
    class deleteImage {
        
        @Test
        public void deleteImage_should_Return204_when_Deleted() throws Exception {
            mockMvc.perform(delete(IMAGES_BY_ID_PATH, fixtures.imageId()))
                   .andExpect(status().isNoContent());
            verify(imageService).deleteById(fixtures.imageId());
        }
        
        @Test
        public void deleteImage_should_Return400_when_ImageIdLessThanOne() throws Exception {
            mockMvc.perform(delete(IMAGES_BY_ID_PATH, fixtures.invalidId()))
                   .andExpect(status().isBadRequest());
            verify(imageService, never()).deleteById(fixtures.invalidId());
        }
    }
}
