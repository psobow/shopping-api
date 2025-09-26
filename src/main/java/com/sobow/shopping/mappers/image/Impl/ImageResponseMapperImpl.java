package com.sobow.shopping.mappers.image.Impl;

import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.dto.ImageResponse;
import com.sobow.shopping.mappers.image.ImageResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class ImageResponseMapperImpl implements ImageResponseMapper {
    
    
    @Override
    public ImageResponse mapToDto(Image image) {
        return new ImageResponse(
            image.getId(),
            image.getFileName(),
            image.getDownloadUrl()
        );
    }
}
