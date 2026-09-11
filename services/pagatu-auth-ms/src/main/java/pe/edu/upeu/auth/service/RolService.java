package pe.edu.upeu.auth.service;

import pe.edu.upeu.auth.dto.RolRequest;
import pe.edu.upeu.auth.dto.RolResponse;
import pe.edu.upeu.auth.entity.Rol;
import pe.edu.upeu.auth.exception.ResourceNotFoundException;
import pe.edu.upeu.auth.exception.DuplicateResourceException;
import pe.edu.upeu.auth.mapper.RolMapper;
import pe.edu.upeu.auth.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    public List<RolResponse> listar() {
        return rolRepository.findAll().stream().map(rolMapper::toResponse).toList();
    }

    public RolResponse obtener(Long id) {
        return rolMapper.toResponse(buscarOFallar(id));
    }

    public RolResponse crear(RolRequest request) {

        if (rolRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("Rol duplicado: " + request.getNombre());
        }
        return rolMapper.toResponse(rolRepository.save(rolMapper.toEntity(request)));
    }

    public RolResponse actualizar(Long id, RolRequest request) {
        Rol entity = buscarOFallar(id);
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        return rolMapper.toResponse(rolRepository.save(entity));
    }

    public void eliminar(Long id) {
        rolRepository.delete(buscarOFallar(id));
    }

    private Rol buscarOFallar(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + id));
    }
}
