package com.raymisupreme.msventas.mapper;

import com.raymisupreme.msventas.dto.ClienteRequest;
import com.raymisupreme.msventas.dto.ClienteResponse;
import com.raymisupreme.msventas.entity.ClienteEntity;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static ClienteEntity toEntity(ClienteRequest request) {
        if (request == null) {
            return null;
        }
        ClienteEntity entity = new ClienteEntity();
        entity.setNombres(request.getNombres());
        entity.setApellidos(request.getApellidos());
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setPais(request.getPais());
        entity.setCiudad(request.getCiudad());
        entity.setDireccionEnvio(request.getDireccionEnvio());
        entity.setKeycloakUserId(request.getKeycloakUserId());
        return entity;
    }

    public static void copyToEntity(ClienteRequest request, ClienteEntity entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNombres(request.getNombres());
        entity.setApellidos(request.getApellidos());
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setPais(request.getPais());
        entity.setCiudad(request.getCiudad());
        entity.setDireccionEnvio(request.getDireccionEnvio());
        if (request.getKeycloakUserId() != null) entity.setKeycloakUserId(request.getKeycloakUserId());
    }

    public static ClienteResponse toResponse(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ClienteResponse(
                entity.getIdCliente(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPais(),
                entity.getCiudad(),
                entity.getDireccionEnvio(),
                entity.getKeycloakUserId()
        );
    }
}
