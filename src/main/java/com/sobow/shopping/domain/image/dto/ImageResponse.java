package com.sobow.shopping.domain.image.dto;

public record ImageResponse(
    Long id,
    String fileName,
    String downloadUrl
) {

}
