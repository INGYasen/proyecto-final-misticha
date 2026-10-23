package com.raymisupreme.mscatalogo.service;

import com.raymisupreme.mscatalogo.dto.PrecioRequest;
import com.raymisupreme.mscatalogo.dto.PrecioResponse;

import java.util.List;

public interface PrecioService {
    List<PrecioResponse> listarActivos();
    PrecioResponse obtenerPorPrendaId(Long prendaId);
    PrecioResponse crear(PrecioRequest request);
    PrecioResponse actualizar(Long id, PrecioRequest request);
    void eliminar(Long id);
}
