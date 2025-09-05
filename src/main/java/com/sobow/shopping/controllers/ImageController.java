package com.sobow.shopping.controllers;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.dto.FileContent;
import com.sobow.shopping.domain.dto.ImageDto;
import com.sobow.shopping.domain.responses.ApiResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {
    
    private final ImageService imageService;
    private final Mapper<Image, ImageDto> imageMapper;
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImages(@RequestParam List<MultipartFile> files,
                                                  @RequestParam Long productId) {
        
        List<ImageDto> imageDtoList = imageService.saveImages(files, productId)
                                                  .stream()
                                                  .map(imageMapper::mapToDto)
                                                  .toList();
        
        return new ResponseEntity<>(
            new ApiResponse("Upload success", imageDtoList),
            HttpStatus.CREATED
        );
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long id) {
        FileContent fileContent = imageService.getImageContent(id);
        ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent.bytes());
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType(fileContent.contentType()))
                             .contentLength(fileContent.length())
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileContent.filename() + "\"")
                             .body(byteArrayResource);
    }
}
