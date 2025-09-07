package com.sobow.shopping.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
    
    private final static long existingId = 1L;
    private final static long invalidId = 0L;
    private final static long nonExistingId = 999L;
    
    private static MockMultipartFile getValidMultipartFile() {
        return new MockMultipartFile(
            "file", "a.png", "image/png", new byte[]{1});
    }
    
    @Test
    public void deleteImage_should_Return204_when_Deleted() throws Exception {
        mockMvc.perform(delete("/api/images/{id}", existingId))
               .andExpect(status().isNoContent());
        verify(imageService).deleteById(existingId);
    }
    
    @Nested
    @DisplayName("saveImages")
    class saveImages {
        
        @Test
        public void saveImages_should_Return201WithList_when_ValidBatch() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            
            List<Image> saved = List.of(new Image());
            ImageResponse dto = new ImageResponse(1L, "", "/api/images/1");
            
            when(imageService.saveImages(anyList(), eq(existingId))).thenReturn(saved);
            when(imageMapper.mapToDto(any())).thenReturn(dto);
            
            mockMvc.perform(multipart("/api/products/{productId}/images", existingId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isCreated())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.message").value("Upload success"))
                   .andExpect(jsonPath("$.data[0]").exists())
                   .andExpect(jsonPath("$.data[0].downloadUrl")
                                  .value("/api/images/1"));
            
            verify(imageService).saveImages(anyList(), eq(existingId));
        }
        
        @Test
        public void saveImages_should_Return400_when_FilePartMissing() throws Exception {
            mockMvc.perform(multipart("/api/products/{productId}/images", existingId)
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
            
            mockMvc.perform(multipart("/api/products/{productId}/images", existingId)
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
            
            when(imageService.saveImages(anyList(), eq(nonExistingId)))
                .thenThrow(new EntityNotFoundException());
            
            mockMvc.perform(multipart("/api/products/{productId}/images", nonExistingId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isNotFound());
        }
        
        @Test
        public void saveImages_should_Return415_when_ContentTypeUnsupported() throws Exception {
            mockMvc.perform(multipart("/api/products/{productId}/images", existingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                   .andExpect(status().isUnsupportedMediaType());
            verify(imageService, never()).saveImages(anyList(), anyLong());
        }
        
        @Test
        public void saveImages_should_Return413PayloadTooLarge_when_FileExceedsLimit() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            
            when(imageService.saveImages(anyList(), eq(existingId)))
                .thenThrow(new MaxUploadSizeExceededException(DataSize.ofMegabytes(5).toBytes()));
            
            mockMvc.perform(multipart("/api/products/{productId}/images", existingId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isPayloadTooLarge())
                   .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                   .andExpect(jsonPath("$.status").value(413))
                   .andExpect(jsonPath("$.path").value("/api/products/" + existingId + "/images"));
        }
    }
    
    @Nested
    @DisplayName("downloadImage")
    class downloadImage {
        
        @Test
        public void downloadImage_should_Return200WithFile_when_IdValid() throws Exception {
            byte[] bytes = new byte[]{1, 2, 3};
            FileContent fileContent = new FileContent(
                "photo.png",
                "image/png",
                bytes.length,
                bytes);
            
            when(imageService.getImageContent(existingId)).thenReturn(fileContent);
            
            mockMvc.perform(get("/api/images/{id}", existingId))
                   .andExpect(status().isOk())
                   .andExpect(header().string("Content-Type", "image/png"))
                   .andExpect(header().string("Content-Length", String.valueOf(bytes.length)))
                   .andExpect(header().string("Content-Disposition", "attachment; filename=\"photo.png\""))
                   .andExpect(content().bytes(bytes));
        }
        
        @Test
        public void downloadImage_should_Return400_when_IdLessThanOne() throws Exception {
            mockMvc.perform(get("/api/images/{id}", invalidId))
                   .andExpect(status().isBadRequest());
            verify(imageService, never()).getImageContent(anyLong());
        }
        
        @Test
        public void downloadImage_should_Return404_when_IdDoesNotExist() throws Exception {
            when(imageService.getImageContent(nonExistingId)).thenThrow(new EntityNotFoundException());
            mockMvc.perform(get("/api/images/{id}", nonExistingId))
                   .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("updateImage")
    class updateImage {
        
        @Test
        public void updateImage_should_Return200WithDto_when_Valid() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            Image updated = new Image();
            when(imageService.updateById(file, existingId)).thenReturn(updated);
            when(imageMapper.mapToDto(updated)).thenReturn(
                new ImageResponse(existingId, file.getOriginalFilename(), "/api/images" + existingId));
            
            mockMvc.perform(multipart(HttpMethod.PUT, "/api/images/{id}", existingId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.message").value("Updated"))
                   .andExpect(jsonPath("$.data.id").value(existingId));
        }
        
        @Test
        public void updateImage_should_Return400_when_FilePartMissing() throws Exception {
            mockMvc.perform(multipart(HttpMethod.PUT, "/api/images/{id}", existingId)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
            verify(imageService, never()).updateById(any(), anyLong());
        }
        
        @Test
        public void updateImage_should_Return400_when_IdLessThanOne() throws Exception {
            MockMultipartFile file = getValidMultipartFile();
            mockMvc.perform(multipart(HttpMethod.PUT, "/api/images/{id}", invalidId)
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void updateImage_should_Return415_when_ContentTypeUnsupported() throws Exception {
            mockMvc.perform(put("/api/images/{id}", existingId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{}"))
                   .andExpect(status().isUnsupportedMediaType());
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
}
