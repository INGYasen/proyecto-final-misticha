package com.raymisupreme.msacopio.service;

import com.raymisupreme.msacopio.dto.LoteCompraRequest;
import com.raymisupreme.msacopio.dto.LoteCompraResponse;
import java.util.List;

public interface LoteCompraService {
    LoteCompraResponse registrarLote(LoteCompraRequest request);
    List<LoteCompraResponse> listarTodos();
    LoteCompraResponse buscarPorId(Long id);
    LoteCompraResponse actualizar(Long id, LoteCompraRequest request);
    void eliminar(Long id);
}
