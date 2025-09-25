package com.sobow.shopping.mappers.image.Impl;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.ImageResponse;
import com.sobow.shopping.mappers.image.ImageResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class ImageResponseMapperImpl implements ImageResponseMapper {
    
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
