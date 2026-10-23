package com.raymisupreme.msacopio.mapper;

import com.raymisupreme.msacopio.dto.LoteCompraRequest;
import com.raymisupreme.msacopio.dto.LoteCompraResponse;
import com.raymisupreme.msacopio.entity.LoteCompraEntity;
import org.springframework.stereotype.Component;

@Component
public class LoteCompraMapper {

    public LoteCompraEntity toEntity(LoteCompraRequest request) {
        LoteCompraEntity entity = new LoteCompraEntity();
        // Nota: el idArtesano no lo seteamos aquí porque requiere buscar en la BD.
        // Eso ya lo hicimos en la clase LoteCompraServiceImpl.
        entity.setFechaCompra(request.getFechaCompra());
        entity.setPrecioTotalPagado(request.getPrecioTotalPagado());
        entity.setCantidadTotalRecibida(request.getCantidadTotalRecibida());
        entity.setDescripcionGeneral(request.getDescripcionGeneral());
        entity.setEstadoLote(request.getEstadoLote());
        return entity;
    }

    public LoteCompraResponse toResponse(LoteCompraEntity entity) {
        LoteCompraResponse response = new LoteCompraResponse();
        response.setIdLote(entity.getIdLote());

        // Extraemos los datos del artesano si existe
        if (entity.getArtesano() != null) {
            response.setIdArtesano(entity.getArtesano().getIdArtesano());
            response.setNombreArtesano(entity.getArtesano().getNombres() + " " + entity.getArtesano().getApellidos());
        }

        response.setFechaCompra(entity.getFechaCompra());
        response.setPrecioTotalPagado(entity.getPrecioTotalPagado());
        response.setCantidadTotalRecibida(entity.getCantidadTotalRecibida());
        response.setDescripcionGeneral(entity.getDescripcionGeneral());
        response.setEstadoLote(entity.getEstadoLote());

        return response;
    }
}