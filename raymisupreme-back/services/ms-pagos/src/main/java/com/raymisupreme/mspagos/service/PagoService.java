package com.raymisupreme.mspagos.service;

import com.raymisupreme.mspagos.dto.PagoEstadoRequest;
import com.raymisupreme.mspagos.dto.PagoRequest;
import com.raymisupreme.mspagos.dto.PagoResponse;

import java.util.List;

public interface PagoService {

    PagoResponse procesarPago(PagoRequest request);

    PagoResponse obtenerPorId(Long id);

    PagoResponse obtenerPorPedidoId(Long pedidoId);

    PagoResponse actualizarEstado(Long id, PagoEstadoRequest request);

    List<PagoResponse> listarTodos();
}
