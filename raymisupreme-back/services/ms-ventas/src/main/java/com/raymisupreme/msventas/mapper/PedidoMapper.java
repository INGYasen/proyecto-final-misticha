package com.raymisupreme.msventas.mapper;

import com.raymisupreme.msventas.dto.PedidoRequest;
import com.raymisupreme.msventas.dto.PedidoResponse;
import com.raymisupreme.msventas.entity.ClienteEntity;
import com.raymisupreme.msventas.entity.PedidoEntity;

public final class PedidoMapper {

    private PedidoMapper() {
    }

    public static PedidoEntity toEntity(PedidoRequest request) {
        if (request == null) {
            return null;
        }
        PedidoEntity entity = new PedidoEntity();

        if (request.getIdCliente() != null) {
            ClienteEntity clienteRef = new ClienteEntity();
            clienteRef.setIdCliente(request.getIdCliente());
            entity.setCliente(clienteRef);
        } else {
            entity.setCliente(null);
        }

        entity.setFechaPedido(request.getFechaPedido());
        entity.setEstadoPedido(request.getEstadoPedido());
        entity.setTotalPagar(request.getTotalPagar());
        return entity;
    }

    public static void copyToEntity(PedidoRequest request, PedidoEntity entity) {
        if (request == null || entity == null) {
            return;
        }

        if (request.getIdCliente() != null) {
            ClienteEntity clienteRef = new ClienteEntity();
            clienteRef.setIdCliente(request.getIdCliente());
            entity.setCliente(clienteRef);
        }

        entity.setFechaPedido(request.getFechaPedido());
        entity.setEstadoPedido(request.getEstadoPedido());
        entity.setTotalPagar(request.getTotalPagar());
    }

    public static PedidoResponse toResponse(PedidoEntity entity) {
        if (entity == null) {
            return null;
        }
        Long idCliente = entity.getCliente() != null ? entity.getCliente().getIdCliente() : null;
        return new PedidoResponse(
                entity.getIdPedido(),
                idCliente,
                ClienteMapper.toResponse(entity.getCliente()),
                entity.getFechaPedido(),
                entity.getEstadoPedido(),
                entity.getTotalPagar()
        );
    }
}
