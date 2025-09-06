package com.sobow.shopping.domain.dto;

public record FileContent(String fileName, String fileType, long length, byte[] bytes) {

}

