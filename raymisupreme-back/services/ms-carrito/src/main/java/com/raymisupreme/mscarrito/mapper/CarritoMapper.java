package com.raymisupreme.mscarrito.mapper;

import com.raymisupreme.mscarrito.dto.CarritoResponse;
import com.raymisupreme.mscarrito.entity.CarritoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ItemCarritoMapper.class)
public interface CarritoMapper {

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "totalCarrito", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    CarritoResponse toResponse(CarritoEntity entity);
}
