package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.dto.FileContent;
import com.sobow.shopping.domain.dto.ImageDto;
import com.sobow.shopping.domain.responses.ApiResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ImageService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}")
public class ImageController {
    
    private final ImageService imageService;
    private final Mapper<Image, ImageDto> imageMapper;
    
    @PostMapping(
        path = "/products/{productId}/images",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> saveImages(
        @RequestPart("file") @NotEmpty List<@NotNull MultipartFile> files,
        @PathVariable @Min(1) Long productId) {
        
        List<ImageDto> imageDtoList = imageService.saveImages(files, productId)
                                                  .stream()
                                                  .map(imageMapper::mapToDto)
                                                  .toList();
        
        return new ResponseEntity<>(
            new ApiResponse("Upload success", imageDtoList),
            HttpStatus.CREATED
        );
    }
    
    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable @Min(1) Long id) {
        FileContent fileContent = imageService.getImageContent(id);
        ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent.bytes());
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType(fileContent.fileType()))
                             .contentLength(fileContent.length())
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileContent.fileName() + "\"")
                             .body(byteArrayResource);
    }
    
    @PutMapping(
        path = "/images/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateImage(
        @PathVariable @Min(1) Long id,
        @RequestPart("file") @NotNull MultipartFile file) {
        Image updatedImage = imageService.updateById(file, id);
        return ResponseEntity.ok(
            new ApiResponse("Updated", imageMapper.mapToDto(updatedImage))
        );
    }
    
    @DeleteMapping("/images/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
