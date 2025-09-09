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

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.dto.FileContent;
import com.sobow.shopping.domain.responses.ImageResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ImageService;
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
    
    private final static long productId = 1L;
    private final static long imageId = 2L;
    private final static long invalidId = 0L;
    private final static long nonExistingId = 999L;
    
    private static MockMultipartFile getValidMultipartFile() {
        return new MockMultipartFile(
            "file", "photo.png", "image/png", new byte[]{1});
    }
    
    @Nested
    @DisplayName("saveImages")
    class saveImages {
        
        @Test
        public void saveImages_should_Return201WithList_when_ValidRequest() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            
            List<Image> saved = List.of(new Image());
            Long newImageId = 1L;
            ImageResponse dto = new ImageResponse(newImageId, file.getOriginalFilename(), "/api/images/" + newImageId);
            
            when(imageService.saveImages(List.of(file), productId)).thenReturn(saved);
            when(imageMapper.mapToDto(saved.get(0))).thenReturn(dto);
            
            mockMvc.perform(multipart("/api/products/{productId}/images", productId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isCreated())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.message").value("Upload success"))
                   .andExpect(jsonPath("$.data[0].id").value(newImageId))
                   .andExpect(jsonPath("$.data[0].fileName").value(file.getOriginalFilename()))
                   .andExpect(jsonPath("$.data[0].downloadUrl")
                                  .value("/api/images/" + newImageId));
        }
        
        @Test
        public void saveImages_should_Return400_when_FilePartMissing() throws Exception {
            mockMvc.perform(multipart("/api/products/{productId}/images", productId)
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
            
            mockMvc.perform(multipart("/api/products/{productId}/images", productId)
                                .file(badFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).saveImages(anyList(), anyLong());
        }
        
        @Test
        public void saveImages_should_Return400_when_ProductIdLessThanOne() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            
            mockMvc.perform(multipart("/api/products/{productId}/images", invalidId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).saveImages(anyList(), anyLong());
        }
        
        @Test
        public void saveImages_should_Return404_when_ProductIdDoesNotExist() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            
            when(imageService.saveImages(List.of(file), nonExistingId))
                .thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(multipart("/api/products/{productId}/images", nonExistingId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void saveImages_should_Return415_when_ContentTypeUnsupported() throws Exception {
            mockMvc.perform(multipart("/api/products/{productId}/images", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                   .andExpect(status().isUnsupportedMediaType());
            
            verify(imageService, never()).saveImages(anyList(), anyLong());
        }
        
        @Test
        public void saveImages_should_Return413_when_FileExceedsLimit() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            
            when(imageService.saveImages(List.of(file), productId))
                .thenThrow(new MaxUploadSizeExceededException(DataSize.ofMegabytes(5).toBytes()));
            
            mockMvc.perform(multipart("/api/products/{productId}/images", productId)
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
            MockMultipartFile file = getValidMultipartFile();
            Image updated = new Image();
            
            when(imageService.updateById(file, imageId)).thenReturn(updated);
            when(imageMapper.mapToDto(updated)).thenReturn(
                new ImageResponse(imageId, file.getOriginalFilename(), "/api/images" + imageId));
            
            mockMvc.perform(multipart(HttpMethod.PUT, "/api/images/{id}", imageId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.message").value("Updated"))
                   .andExpect(jsonPath("$.data.id").value(imageId))
                   .andExpect(jsonPath("$.data.fileName").value(file.getOriginalFilename()))
                   .andExpect(jsonPath("$.data.downloadUrl").value("/api/images" + imageId));
        }
        
        @Test
        public void updateImage_should_Return400_when_FilePartMissing() throws Exception {
            mockMvc.perform(multipart(HttpMethod.PUT, "/api/images/{id}", imageId)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).updateById(any(), anyLong());
        }
        
        @Test
        public void updateImage_should_Return400_when_ImageIdLessThanOne() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            mockMvc.perform(multipart(HttpMethod.PUT, "/api/images/{id}", invalidId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).updateById(any(), anyLong());
        }
        
        @Test
        public void updateImage_should_Return415_when_ContentTypeUnsupported() throws Exception {
            mockMvc.perform(put("/api/images/{id}", imageId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{}"))
                   .andExpect(status().isUnsupportedMediaType());
            
            verify(imageService, never()).updateById(any(), anyLong());
        }
        
        @Test
        public void updateImage_should_Return404_when_ImageIdDoesNotExist() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            when(imageService.updateById(file, nonExistingId)).thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(multipart(HttpMethod.PUT, "/api/images/{id}", nonExistingId)
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
            byte[] bytes = new byte[]{1, 2, 3};
            FileContent fileContent = new FileContent(
                "photo.png",
                "image/png",
                bytes.length,
                bytes
            );
            
            when(imageService.getImageContent(imageId)).thenReturn(fileContent);
            
            mockMvc.perform(get("/api/images/{id}", imageId))
                   .andExpect(status().isOk())
                   .andExpect(header().string("Content-Type", "image/png"))
                   .andExpect(header().string("Content-Length", String.valueOf(bytes.length)))
                   .andExpect(header().string("Content-Disposition", "attachment; filename=\"photo.png\""))
                   .andExpect(content().bytes(bytes));
        }
        
        @Test
        public void downloadImage_should_Return400_when_ImageIdLessThanOne() throws Exception {
            mockMvc.perform(get("/api/images/{id}", invalidId))
                   .andExpect(status().isBadRequest());
            
            verify(imageService, never()).getImageContent(anyLong());
        }
        
        @Test
        public void downloadImage_should_Return404_when_ImageIdDoesNotExist() throws Exception {
            when(imageService.getImageContent(nonExistingId)).thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(get("/api/images/{id}", nonExistingId))
                   .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("deleteImage")
    class deleteImage {
        
        @Test
        public void deleteImage_should_Return204_when_Deleted() throws Exception {
            mockMvc.perform(delete("/api/images/{id}", imageId))
                   .andExpect(status().isNoContent());
            verify(imageService).deleteById(imageId);
        }
        
        @Test
        public void deleteImage_should_Return400_when_ImageIdLessThanOne() throws Exception {
            mockMvc.perform(delete("/api/images/{id}", invalidId))
                   .andExpect(status().isBadRequest());
            verify(imageService, never()).deleteById(invalidId);
        }
    }
}
