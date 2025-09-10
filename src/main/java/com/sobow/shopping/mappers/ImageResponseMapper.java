package com.sobow.shopping.mappers;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.responses.ImageResponse;
import org.springframework.stereotype.Component;

@Component
public class ImageResponseMapper implements Mapper<Image, ImageResponse> {
    
    @Override
    public Image mapToEntity(ImageResponse imageResponse) {
        throw new UnsupportedOperationException("mapToEntity is not implemented yet");
    }
    
    @Override
    public ImageResponse mapToDto(Image image) {
        return new ImageResponse(
            image.getId(),
            image.getFileName(),
            image.getDownloadUrl()
        );
    }
}
