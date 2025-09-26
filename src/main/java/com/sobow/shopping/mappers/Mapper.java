package com.sobow.shopping.mappers;

public interface Mapper<Entity, DTO> {
    
    default Entity mapToEntity(DTO dto) {
        throw new UnsupportedOperationException("mapToEntity not supported");
    }
    
    default DTO mapToDto(Entity entity) {
        throw new UnsupportedOperationException("mapToDto not supported");
    }
}
