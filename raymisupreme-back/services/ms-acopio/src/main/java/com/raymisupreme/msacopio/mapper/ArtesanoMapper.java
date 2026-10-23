package com.raymisupreme.msacopio.mapper;

import com.raymisupreme.msacopio.dto.ArtesanoRequest;
import com.raymisupreme.msacopio.dto.ArtesanoResponse;
import com.raymisupreme.msacopio.entity.ArtesanoEntity;
import org.springframework.stereotype.Component;

@Component
public class ArtesanoMapper {

    // Convierte el formulario de internet (Request) hacia la Base de Datos (Entity)
    public ArtesanoEntity toEntity(ArtesanoRequest request) {
        ArtesanoEntity entity = new ArtesanoEntity();
        entity.setNombres(request.getNombres());
        entity.setApellidos(request.getApellidos());
        entity.setDni(request.getDni());
        entity.setComunidadOZona(request.getComunidadOZona());
        entity.setTelefono(request.getTelefono());
        return entity;
    }

    // Convierte la Base de Datos (Entity) hacia la pantalla del usuario (Response)
    public ArtesanoResponse toResponse(ArtesanoEntity entity) {
        ArtesanoResponse response = new ArtesanoResponse();
        response.setIdArtesano(entity.getIdArtesano());
        response.setNombres(entity.getNombres());
        response.setApellidos(entity.getApellidos());
        response.setDni(entity.getDni());
        response.setComunidadOZona(entity.getComunidadOZona());
        response.setTelefono(entity.getTelefono());
        return response;
    }
}