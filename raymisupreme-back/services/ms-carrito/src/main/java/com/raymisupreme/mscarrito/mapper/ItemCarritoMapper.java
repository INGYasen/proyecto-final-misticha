package com.raymisupreme.mscarrito.mapper;

import com.raymisupreme.mscarrito.dto.ItemCarritoResponse;
import com.raymisupreme.mscarrito.entity.ItemCarritoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemCarritoMapper {

    ItemCarritoResponse toResponse(ItemCarritoEntity entity);
}
