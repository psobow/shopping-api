package com.sobow.shopping.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.dto.ImageDto;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ImageService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ImageController.class)
public class ImageControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ImageService imageService;
    
    @MockitoBean
    private Mapper<Image, ImageDto> imageMapper;
    
    @Test
    public void saveImages_Success() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
            "files", "a.png", "image/png", new byte[]{1});
        MockMultipartFile file2 = new MockMultipartFile(
            "files", "b.png", "image/png", new byte[]{2});
        
        List<Image> saved = List.of(new Image());
        ImageDto dto = new ImageDto(1L, "", "/api/images/1");
        
        long productId = 10L;
        
        when(imageService.saveImages(anyList(), eq(productId))).thenReturn(saved);
        when(imageMapper.mapToDto(any())).thenReturn(dto);
        
        mockMvc.perform(multipart("/api/products/{productId}/images", productId)
                            .file(file1).file(file2)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.message").value("Upload success"))
               .andExpect(jsonPath("$.data[0]").exists())
               .andExpect(jsonPath("$.data[0].downloadUrl")
                              .value("/api/images/1"));
    }
}
