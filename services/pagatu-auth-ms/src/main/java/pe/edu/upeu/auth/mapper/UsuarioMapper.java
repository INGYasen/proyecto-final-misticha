package pe.edu.upeu.auth.mapper;

import pe.edu.upeu.auth.dto.UsuarioRequest;
import pe.edu.upeu.auth.dto.UsuarioResponse;
import pe.edu.upeu.auth.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {
        return Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .nombre(request.getNombre())
                .activo(request.getActivo())
                .rolId(request.getRolId())
                .build();
    }

    public UsuarioResponse toResponse(Usuario entity) {
        return UsuarioResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .nombre(entity.getNombre())
                .activo(entity.getActivo())
                .rolId(entity.getRolId())
                .build();
    }
}
