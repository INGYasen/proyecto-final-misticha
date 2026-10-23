package com.raymisupreme.msinventario.mapper;
import com.raymisupreme.msinventario.dto.PrendaRequest;
import com.raymisupreme.msinventario.dto.PrendaResponse;
import com.raymisupreme.msinventario.entity.CategoriaEntity;
import com.raymisupreme.msinventario.entity.PrendaEntity;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;

public final class PrendaMapper {
    private PrendaMapper() {}

    public static PrendaEntity toEntity(PrendaRequest request) {
        if (request == null) return null;
        PrendaEntity entity = new PrendaEntity();
        entity.setIdLote(request.getIdLote());
        entity.setCodigoUnico(request.getCodigoUnico());
        entity.setNombrePrenda(request.getNombrePrenda());
        entity.setPublico(request.getPublico() != null ? request.getPublico() : PublicoObjetivo.UNISEX);
        entity.setTalla(request.getTalla());
        entity.setColor(request.getColor());
        entity.setMaterial(request.getMaterial());
        entity.setStock(request.getStock() != null ? request.getStock() : 0);
        entity.setUrlFoto(request.getUrlFoto());

        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setIdCategoria(request.getIdCategoria());
        entity.setCategoria(categoria);
        return entity;
    }

    public static PrendaResponse toResponse(PrendaEntity entity) {
        if (entity == null) return null;
        Long idCat = (entity.getCategoria() != null) ? entity.getCategoria().getIdCategoria() : null;
        return new PrendaResponse(
                entity.getIdPrenda(),
                entity.getIdLote(),
                idCat,
                entity.getCategoria() != null ? entity.getCategoria().getNombre() : null,
                entity.getPublico(),
                entity.getCodigoUnico(),
                entity.getNombrePrenda(),
                entity.getTalla(),
                entity.getColor(),
                entity.getMaterial(),
                entity.getStock(),
                entity.getUrlFoto()
        );
    }
}
