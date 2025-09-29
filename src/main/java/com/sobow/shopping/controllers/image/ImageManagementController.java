package com.sobow.shopping.controllers.image;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.image.dto.ImageResponse;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.mappers.image.ImageResponseMapper;
import com.sobow.shopping.services.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
    name = "Image Management Controller",
    description = "API to manage images by Admin"
)
public class ImageManagementController {
    
    private final ImageService imageService;
    
    private final ImageResponseMapper imageResponseMapper;
    
    @Operation(
        summary = "Upload product images",
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
    )
    @Parameters({
        @Parameter(name = "productId", required = true, description = "Product ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Upload success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "415", description = "Unsupported media type")
    })
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
    
    @Operation(
        summary = "Update product image",
        security = @SecurityRequirement(name = "bearerAuth"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
    )
    @Parameters({
        @Parameter(name = "productId", required = true, description = "Product ID"),
        @Parameter(name = "imageId", required = true, description = "Image ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Image not found"),
        @ApiResponse(responseCode = "415", description = "Unsupported media type")
    })
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
    
    @Operation(
        summary = "Delete product image",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameters({
        @Parameter(name = "productId", required = true, description = "Product ID"),
        @Parameter(name = "imageId", required = true, description = "Image ID")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
        @PathVariable @Positive long productId,
        @PathVariable @Positive long imageId
    ) {
        imageService.deleteByProductIdAndId(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
