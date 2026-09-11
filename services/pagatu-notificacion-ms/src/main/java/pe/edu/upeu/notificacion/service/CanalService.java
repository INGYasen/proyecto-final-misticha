package pe.edu.upeu.notificacion.service;

import pe.edu.upeu.notificacion.dto.CanalRequest;
import pe.edu.upeu.notificacion.dto.CanalResponse;
import pe.edu.upeu.notificacion.entity.Canal;
import pe.edu.upeu.notificacion.exception.ResourceNotFoundException;
import pe.edu.upeu.notificacion.exception.DuplicateResourceException;
import pe.edu.upeu.notificacion.mapper.CanalMapper;
import pe.edu.upeu.notificacion.repository.CanalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CanalService {

    private final CanalRepository canalRepository;
    private final CanalMapper canalMapper;

    public List<CanalResponse> listar() {
        return canalRepository.findAll().stream().map(canalMapper::toResponse).toList();
    }

    public CanalResponse obtener(Long id) {
        return canalMapper.toResponse(buscarOFallar(id));
    }

    public CanalResponse crear(CanalRequest request) {

        if (canalRepository.existsByCodigo(request.getCodigo())) {
            throw new DuplicateResourceException("Canal duplicado: " + request.getCodigo());
        }
        return canalMapper.toResponse(canalRepository.save(canalMapper.toEntity(request)));
    }

    public CanalResponse actualizar(Long id, CanalRequest request) {
        Canal entity = buscarOFallar(id);
        entity.setCodigo(request.getCodigo());
        entity.setNombre(request.getNombre());
        entity.setActivo(request.getActivo());
        return canalMapper.toResponse(canalRepository.save(entity));
    }

    public void eliminar(Long id) {
        canalRepository.delete(buscarOFallar(id));
    }

    private Canal buscarOFallar(Long id) {
        return canalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Canal no encontrado: " + id));
    }
}
