package com.raymisupreme.mscatalogo.mapper;

import com.raymisupreme.mscatalogo.dto.DescuentoRequest;
import com.raymisupreme.mscatalogo.dto.DescuentoResponse;
import com.raymisupreme.mscatalogo.entity.DescuentoEntity;
import org.springframework.stereotype.Component;

@Component
public class DescuentoMapper {

    public DescuentoEntity toEntity(DescuentoRequest request) {
        if (request == null) {
            return null;
        }
        return DescuentoEntity.builder()
                .codigo(request.getCodigo())
                .descripcion(request.getDescripcion())
                .porcentaje(request.getPorcentaje())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();
    }

    public DescuentoResponse toResponse(DescuentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return DescuentoResponse.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .descripcion(entity.getDescripcion())
                .porcentaje(entity.getPorcentaje())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .activo(entity.getActivo())
                .build();
    }
}
