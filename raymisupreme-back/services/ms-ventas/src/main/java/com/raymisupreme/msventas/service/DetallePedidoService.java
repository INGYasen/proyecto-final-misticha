package com.raymisupreme.msventas.service;

import com.raymisupreme.msventas.dto.DetallePedidoRequest;
import com.raymisupreme.msventas.dto.DetallePedidoResponse;
import java.util.List;

public interface DetallePedidoService {
    DetallePedidoResponse crear(DetallePedidoRequest request);
    DetallePedidoResponse obtenerPorId(Long idDetalle);
    List<DetallePedidoResponse> listar();
    List<DetallePedidoResponse> listarPorPedido(Long idPedido);
    DetallePedidoResponse actualizar(Long idDetalle, DetallePedidoRequest request);
    void eliminar(Long idDetalle);
}

