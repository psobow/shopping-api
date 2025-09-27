package com.sobow.shopping.controllers.image.dto;

public record ImageResponse(
    Long id,
    String fileName,
    String fileType,
    String downloadUrl
) {

}
