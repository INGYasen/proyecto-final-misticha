package com.raymisupreme.msinventario.mapper;

import com.raymisupreme.msinventario.dto.CategoriaRequest;
import com.raymisupreme.msinventario.dto.CategoriaResponse;
import com.raymisupreme.msinventario.entity.CategoriaEntity;

public final class CategoriaMapper {

    private CategoriaMapper() {
    }

    public static CategoriaEntity toEntity(CategoriaRequest request) {
        if (request == null) return null;

        CategoriaEntity entity = new CategoriaEntity();
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        return entity;
    }

    public static CategoriaResponse toResponse(CategoriaEntity entity) {
        if (entity == null) return null;

        return new CategoriaResponse(
                entity.getIdCategoria(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }
}