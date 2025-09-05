package com.sobow.shopping.mappers;

public interface Mapper<Entity, DTO> {

    Entity mapToEntity(DTO dto);
}
