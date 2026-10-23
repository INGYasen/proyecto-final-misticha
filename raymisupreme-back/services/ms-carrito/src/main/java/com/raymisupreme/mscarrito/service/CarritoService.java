package com.raymisupreme.mscarrito.service;

import com.raymisupreme.mscarrito.dto.CarritoResponse;
import com.raymisupreme.mscarrito.dto.ItemCarritoRequest;

public interface CarritoService {

    CarritoResponse obtenerCarrito(Long clienteId);

    CarritoResponse agregarItem(Long clienteId, ItemCarritoRequest request);

    CarritoResponse actualizarCantidad(Long clienteId, Long itemId, Integer cantidad);

    void vaciarCarrito(Long clienteId);

    CarritoResponse convertirCarrito(Long clienteId);
}
