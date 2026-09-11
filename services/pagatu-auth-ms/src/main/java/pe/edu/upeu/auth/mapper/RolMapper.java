package pe.edu.upeu.auth.mapper;

import pe.edu.upeu.auth.dto.RolRequest;
import pe.edu.upeu.auth.dto.RolResponse;
import pe.edu.upeu.auth.entity.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {

    public Rol toEntity(RolRequest request) {
        return Rol.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();
    }

    public RolResponse toResponse(Rol entity) {
        return RolResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
