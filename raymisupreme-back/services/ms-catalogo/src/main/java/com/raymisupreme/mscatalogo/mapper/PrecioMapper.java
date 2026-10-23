package com.raymisupreme.mscatalogo.mapper;

import com.raymisupreme.mscatalogo.dto.PrecioRequest;
import com.raymisupreme.mscatalogo.dto.PrecioResponse;
import com.raymisupreme.mscatalogo.entity.PrecioEntity;
import org.springframework.stereotype.Component;

@Component
public class PrecioMapper {

    private final DescuentoMapper descuentoMapper;

    public PrecioMapper(DescuentoMapper descuentoMapper) {
        this.descuentoMapper = descuentoMapper;
    }

    public PrecioEntity toEntity(PrecioRequest request) {
        if (request == null) {
            return null;
        }
        return PrecioEntity.builder()
                .prendaId(request.getPrendaId())
                .precioBase(request.getPrecioBase())
                .build();
    }

    public PrecioResponse toResponse(PrecioEntity entity) {
        if (entity == null) {
            return null;
        }
        return PrecioResponse.builder()
                .id(entity.getId())
                .prendaId(entity.getPrendaId())
                .nombre(entity.getNombre())
                .precioBase(entity.getPrecioBase())
                .precioFinal(entity.getPrecioFinal())
                .activo(entity.getActivo())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .descuento(descuentoMapper.toResponse(entity.getDescuento()))
                .build();
    }
}
