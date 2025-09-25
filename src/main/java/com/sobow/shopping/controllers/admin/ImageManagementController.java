package com.sobow.shopping.controllers.admin;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.ImageResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ImageService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin")
public class ImageManagementController {
    
    private final ImageService imageService;
    @Qualifier("imageResponseMapper")
    private final Mapper<Image, ImageResponse> imageResponseMapper;
    
    @PostMapping(
        path = "/products/{productId}/images",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> saveImages(
        @RequestPart("file") @NotEmpty List<MultipartFile> files,
        @PathVariable @Positive long productId
    ) {
        List<ImageResponse> responseList = imageService.saveImages(productId, files)
                                                       .stream()
                                                       .map(imageResponseMapper::mapToDto)
                                                       .toList();
        
        return new ResponseEntity<>(
            new ApiResponse("Upload success", responseList),
            HttpStatus.CREATED
        );
    }
    
    @PutMapping(
        path = "/images/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateImage(
        @RequestPart("file") @NotNull MultipartFile file,
        @PathVariable @Positive long id
    ) {
        Image updatedImage = imageService.updateById(id, file);
        ImageResponse response = imageResponseMapper.mapToDto(updatedImage);
        return ResponseEntity.ok(new ApiResponse("Updated", response));
    }
    
    @DeleteMapping("/images/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable @Positive long id) {
        imageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
