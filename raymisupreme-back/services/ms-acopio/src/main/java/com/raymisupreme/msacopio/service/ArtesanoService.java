package com.raymisupreme.msacopio.service;

import com.raymisupreme.msacopio.dto.ArtesanoRequest;
import com.raymisupreme.msacopio.dto.ArtesanoResponse;
import java.util.List;

public interface ArtesanoService {
    ArtesanoResponse guardar(ArtesanoRequest request);
    List<ArtesanoResponse> listarTodos();
    ArtesanoResponse buscarPorId(Long id);
    ArtesanoResponse actualizar(Long id, ArtesanoRequest request);
    void eliminar(Long id);
}
