package com.sobow.shopping.services.image.Impl;

public record FileContent(
    String fileName,
    String fileType,
    long length,
    byte[] bytes
) {

}

