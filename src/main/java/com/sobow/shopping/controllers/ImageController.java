package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.FileContent;
import com.sobow.shopping.domain.image.dto.ImageResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ImageService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}")
public class ImageController {
    
    private final ImageService imageService;
    private final Mapper<Image, ImageResponse> imageResponseMapper;
    
    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable @Positive long id) {
        FileContent fileContent = imageService.getImageContent(id);
        ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent.bytes());
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType(fileContent.fileType()))
                             .contentLength(fileContent.length())
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileContent.fileName() + "\"")
                             .body(byteArrayResource);
    }
}
