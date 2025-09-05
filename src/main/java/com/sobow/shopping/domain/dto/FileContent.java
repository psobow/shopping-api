package com.sobow.shopping.domain.dto;

public record FileContent(String filename, String contentType, long length, byte[] bytes) {

}

