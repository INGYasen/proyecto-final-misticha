package com.raymisupreme.msventas.service;

import com.raymisupreme.msventas.dto.ClienteRequest;
import com.raymisupreme.msventas.dto.ClienteResponse;
import java.util.List;

public interface ClienteService {
    ClienteResponse crear(ClienteRequest request);
    ClienteResponse obtenerPorId(Long idCliente);
    ClienteResponse obtenerActual(String keycloakUserId, String email);
    List<ClienteResponse> listar();
    ClienteResponse actualizar(Long idCliente, ClienteRequest request);
    void eliminar(Long idCliente);
}
