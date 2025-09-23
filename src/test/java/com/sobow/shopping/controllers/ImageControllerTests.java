package com.sobow.shopping.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sobow.shopping.domain.image.dto.FileContent;
import com.sobow.shopping.services.ImageService;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ImageControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ImageService imageService;
    
    private final static String IMAGES_BY_ID_PATH = "/api/images/{id}";
    
    private final TestFixtures fixtures = new TestFixtures();
    
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
}
