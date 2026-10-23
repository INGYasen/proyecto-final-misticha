package com.raymisupreme.msinventario.service;

import com.raymisupreme.msinventario.dto.PrendaRequest;
import com.raymisupreme.msinventario.dto.PrendaDetalleCompletoResponse;
import com.raymisupreme.msinventario.dto.PrendaResponse;
import com.raymisupreme.msinventario.dto.StockResponse;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;

import java.util.List;

public interface PrendaService {

    PrendaResponse create(PrendaRequest request);

    List<PrendaResponse> getAll(Long idCategoria, PublicoObjetivo publico);

    PrendaResponse getById(Long id);

    PrendaResponse update(Long id, PrendaRequest request);

    void delete(Long id);

    PrendaDetalleCompletoResponse getDetalleCompleto(Long id);

    StockResponse getStock(Long id);

    List<PrendaResponse> getByIdLote(Long idLote);

    void descontarStock(Long id, Integer cantidad);
}
