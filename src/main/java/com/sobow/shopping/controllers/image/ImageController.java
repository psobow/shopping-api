package com.sobow.shopping.controllers.image;

import com.sobow.shopping.services.image.ImageService;
import com.sobow.shopping.services.image.Impl.FileContent;
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
public class ImageController {
    
    private final ImageService imageService;
    
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
