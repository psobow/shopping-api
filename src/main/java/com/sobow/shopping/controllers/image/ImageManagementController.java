package com.sobow.shopping.controllers.image;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.image.dto.ImageResponse;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.mappers.image.ImageResponseMapper;
import com.sobow.shopping.services.image.ImageService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("${api.prefix}/admin/products/{productId}/images")
public class ImageManagementController {
    
    private final ImageService imageService;
    
    private final ImageResponseMapper imageResponseMapper;
    
    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponseDto> saveImages(
        @RequestPart("file") @NotEmpty List<MultipartFile> files,
        @PathVariable @Positive long productId
    ) {
        List<ImageResponse> responseList = imageService.saveImages(productId, files)
                                                       .stream()
                                                       .map(imageResponseMapper::mapToDto)
                                                       .toList();
        
        return new ResponseEntity<>(
            new ApiResponseDto("Upload success", responseList),
            HttpStatus.CREATED
        );
    }
    
    @PutMapping(
        path = "/{imageId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponseDto> updateImage(
        @PathVariable @Positive long productId,
        @PathVariable @Positive long imageId,
        @RequestPart("file") @NotNull MultipartFile file
    ) {
        Image updatedImage = imageService.updateByProductIdAndId(productId, imageId, file);
        ImageResponse response = imageResponseMapper.mapToDto(updatedImage);
        return ResponseEntity.ok(new ApiResponseDto("Updated", response));
    }
    
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
        @PathVariable @Positive long productId,
        @PathVariable @Positive long imageId
    ) {
        imageService.deleteByProductIdAndId(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
