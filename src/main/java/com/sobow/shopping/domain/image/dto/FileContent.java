package com.sobow.shopping.domain.image.dto;

public record FileContent(
    String fileName,
    String fileType,
    long length,
    byte[] bytes
) {

}

