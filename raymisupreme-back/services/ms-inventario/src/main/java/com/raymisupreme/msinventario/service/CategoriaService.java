package com.raymisupreme.msinventario.service;

import com.raymisupreme.msinventario.dto.CategoriaRequest;
import com.raymisupreme.msinventario.dto.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    CategoriaResponse create(CategoriaRequest request);

    List<CategoriaResponse> getAll();

    CategoriaResponse getById(Long id);

    CategoriaResponse update(Long id, CategoriaRequest request);

    void delete(Long id);
}
