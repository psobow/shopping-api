package com.sobow.shopping.mappers;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.domain.dto.ImageDto;

public class ImageMapper implements Mapper<Image, ImageDto> {
    
    @Override
    public Image mapToEntity(ImageDto imageDto) {
        return null;
    }
    
    @Override
    public ImageDto mapToDto(Image image) {
        return new ImageDto(
            image.getId(),
            image.getFileName(),
            image.getDownloadUrl()
        );
    }
}
