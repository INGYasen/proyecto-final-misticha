package com.raymisupreme.mspagos.mapper;

import com.raymisupreme.mspagos.dto.PagoResponse;
import com.raymisupreme.mspagos.entity.PagoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(target = "numeroComprobante", ignore = true)
    PagoResponse toResponse(PagoEntity entity);
}
