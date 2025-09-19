package com.sobow.shopping.domain.image;

public record FileContent(String fileName, String fileType, long length, byte[] bytes) {

}

