package com.raymisupreme.msventas.service;

import com.raymisupreme.msventas.dto.ConfirmarPedidoRequest;
import com.raymisupreme.msventas.dto.CheckoutResponse;
import com.raymisupreme.msventas.dto.PedidoRequest;
import com.raymisupreme.msventas.dto.PedidoResponse;
import java.util.List;

public interface PedidoService {
    PedidoResponse crear(PedidoRequest request);
    PedidoResponse obtenerPorId(Long idPedido);
    List<PedidoResponse> listar();
    List<PedidoResponse> listarPorCliente(Long idCliente);
    List<PedidoResponse> listarDelUsuario(String keycloakUserId, String email);
    void verificarPropietario(Long idPedido, String keycloakUserId, String email);
    PedidoResponse actualizar(Long idPedido, PedidoRequest request);
    PedidoResponse confirmarPedido(Long idPedido, ConfirmarPedidoRequest request);
    PedidoResponse confirmarPagoMercadoPago(Long idPedido);
    PedidoResponse cancelarPedido(Long idPedido);
    CheckoutResponse checkout(Long idCliente);
    void eliminar(Long idPedido);
}
