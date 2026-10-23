package com.raymisupreme.mscatalogo.service;

import com.raymisupreme.mscatalogo.dto.DescuentoRequest;
import com.raymisupreme.mscatalogo.dto.DescuentoResponse;

import java.util.List;

public interface DescuentoService {
    List<DescuentoResponse> listarTodos();
    DescuentoResponse crear(DescuentoRequest request);
    DescuentoResponse actualizar(Long id, DescuentoRequest request);
    void eliminar(Long id);
}
