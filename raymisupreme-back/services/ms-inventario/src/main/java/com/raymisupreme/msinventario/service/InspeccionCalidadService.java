package com.raymisupreme.msinventario.service;

import com.raymisupreme.msinventario.dto.InspeccionCalidadRequest;
import com.raymisupreme.msinventario.dto.InspeccionCalidadResponse;

import java.util.List;

public interface InspeccionCalidadService {

    InspeccionCalidadResponse create(InspeccionCalidadRequest request);

    List<InspeccionCalidadResponse> getAll();

    InspeccionCalidadResponse getById(Long id);

    InspeccionCalidadResponse update(Long id, InspeccionCalidadRequest request);

    void delete(Long id);

    List<InspeccionCalidadResponse> getByIdPrenda(Long idPrenda);
}
