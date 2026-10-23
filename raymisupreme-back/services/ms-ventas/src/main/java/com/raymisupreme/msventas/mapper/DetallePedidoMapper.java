package com.raymisupreme.msventas.mapper;

import com.raymisupreme.msventas.dto.DetallePedidoRequest;
import com.raymisupreme.msventas.dto.DetallePedidoResponse;
import com.raymisupreme.msventas.entity.DetallePedidoEntity;
import com.raymisupreme.msventas.entity.PedidoEntity;

public final class DetallePedidoMapper {

    private DetallePedidoMapper() {
    }

    public static DetallePedidoEntity toEntity(DetallePedidoRequest request) {
        if (request == null) {
            return null;
        }
        DetallePedidoEntity entity = new DetallePedidoEntity();

        if (request.getIdPedido() != null) {
            PedidoEntity pedidoRef = new PedidoEntity();
            pedidoRef.setIdPedido(request.getIdPedido());
            entity.setPedido(pedidoRef);
        } else {
            entity.setPedido(null);
        }

        entity.setIdPrenda(request.getIdPrenda());
        entity.setCantidad(request.getCantidad());
        entity.setPrecioUnitario(request.getPrecioUnitario());
        return entity;
    }

    public static void copyToEntity(DetallePedidoRequest request, DetallePedidoEntity entity) {
        if (request == null || entity == null) {
            return;
        }

        if (request.getIdPedido() != null) {
            PedidoEntity pedidoRef = new PedidoEntity();
            pedidoRef.setIdPedido(request.getIdPedido());
            entity.setPedido(pedidoRef);
        }

        entity.setIdPrenda(request.getIdPrenda());
        entity.setCantidad(request.getCantidad());
        entity.setPrecioUnitario(request.getPrecioUnitario());
    }

    public static DetallePedidoResponse toResponse(DetallePedidoEntity entity) {
        if (entity == null) {
            return null;
        }
        Long idPedido = entity.getPedido() != null ? entity.getPedido().getIdPedido() : null;
        return new DetallePedidoResponse(
                entity.getIdDetalle(),
                idPedido,
                entity.getIdPrenda(),
                entity.getCantidad(),
                entity.getPrecioUnitario()
        );
    }
}

