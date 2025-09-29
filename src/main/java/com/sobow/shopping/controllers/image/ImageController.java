package com.sobow.shopping.controllers.image;

import com.sobow.shopping.services.image.ImageService;
import com.sobow.shopping.services.image.Impl.FileContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("${api.prefix}/products/{productId}/images")
@Tag(
    name = "Image Controller",
    description = "API to download Images"
)
public class ImageController {
    
    private final ImageService imageService;
    
    @Operation(
        summary = "Download product image"
    )
    @Parameters({
        @Parameter(name = "productId", required = true, description = "Product ID"),
        @Parameter(name = "imageId", required = true, description = "Image ID")
    })
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(
                mediaType = "application/octet-stream",
                schema = @Schema(type = "string", format = "binary")
            ),
            headers = {
                @Header(name = "Content-Disposition",
                    description = "e.g. attachment; filename=\"image.jpg\"")
            }
        ),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> downloadImage(
        @PathVariable @Positive long productId,
        @PathVariable @Positive long imageId
    ) {
        FileContent fileContent = imageService.getImageContent(productId, imageId);
        ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent.bytes());
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType(fileContent.fileType()))
                             .contentLength(fileContent.length())
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileContent.fileName() + "\"")
                             .body(byteArrayResource);
    }
}
