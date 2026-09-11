package pe.edu.upeu.notificacion.mapper;

import pe.edu.upeu.notificacion.dto.CanalRequest;
import pe.edu.upeu.notificacion.dto.CanalResponse;
import pe.edu.upeu.notificacion.entity.Canal;
import org.springframework.stereotype.Component;

@Component
public class CanalMapper {

    public Canal toEntity(CanalRequest request) {
        return Canal.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .activo(request.getActivo())
                .build();
    }

    public CanalResponse toResponse(Canal entity) {
        return CanalResponse.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .activo(entity.getActivo())
                .build();
    }
}
