package com.raymisupreme.msinventario.mapper;

import com.raymisupreme.msinventario.dto.InspeccionCalidadRequest;
import com.raymisupreme.msinventario.dto.InspeccionCalidadResponse;
import com.raymisupreme.msinventario.entity.InspeccionCalidadEntity;
import com.raymisupreme.msinventario.entity.PrendaEntity;

public final class InspeccionCalidadMapper {

    private InspeccionCalidadMapper() {
    }

    public static InspeccionCalidadEntity toEntity(InspeccionCalidadRequest request) {
        if (request == null) return null;

        InspeccionCalidadEntity entity = new InspeccionCalidadEntity();
        entity.setFechaInspeccion(request.getFechaInspeccion());
        entity.setResultado(request.getResultado());
        entity.setObservaciones(request.getObservaciones());

        PrendaEntity prenda = new PrendaEntity();
        prenda.setIdPrenda(request.getIdPrenda());
        entity.setPrenda(prenda);

        return entity;
    }

    public static InspeccionCalidadResponse toResponse(InspeccionCalidadEntity entity) {
        if (entity == null) return null;

        Long idPrenda = (entity.getPrenda() != null) ? entity.getPrenda().getIdPrenda() : null;

        return new InspeccionCalidadResponse(
                entity.getIdInspeccionCalidad(),
                entity.getFechaInspeccion(),
                entity.getResultado(),
                entity.getObservaciones(),
                idPrenda
        );
    }
}