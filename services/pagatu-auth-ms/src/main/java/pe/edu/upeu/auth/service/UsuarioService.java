package pe.edu.upeu.auth.service;

import pe.edu.upeu.auth.dto.UsuarioRequest;
import pe.edu.upeu.auth.dto.UsuarioResponse;
import pe.edu.upeu.auth.entity.Usuario;
import pe.edu.upeu.auth.exception.ResourceNotFoundException;
import pe.edu.upeu.auth.exception.DuplicateResourceException;
import pe.edu.upeu.auth.mapper.UsuarioMapper;
import pe.edu.upeu.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(usuarioMapper::toResponse).toList();
    }

    public UsuarioResponse obtener(Long id) {
        return usuarioMapper.toResponse(buscarOFallar(id));
    }

    public UsuarioResponse crear(UsuarioRequest request) {

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Usuario duplicado: " + request.getUsername());
        }
        return usuarioMapper.toResponse(usuarioRepository.save(usuarioMapper.toEntity(request)));
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario entity = buscarOFallar(id);
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setNombre(request.getNombre());
        entity.setActivo(request.getActivo());
        entity.setRolId(request.getRolId());
        return usuarioMapper.toResponse(usuarioRepository.save(entity));
    }

    public void eliminar(Long id) {
        usuarioRepository.delete(buscarOFallar(id));
    }

    private Usuario buscarOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }
}
