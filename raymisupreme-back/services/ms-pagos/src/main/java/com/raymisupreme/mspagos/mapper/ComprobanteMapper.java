package com.raymisupreme.mspagos.mapper;

import com.raymisupreme.mspagos.dto.ComprobanteResponse;
import com.raymisupreme.mspagos.entity.ComprobanteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComprobanteMapper {

    @Mapping(target = "pagoId", source = "pago.id")
    ComprobanteResponse toResponse(ComprobanteEntity entity);
}
