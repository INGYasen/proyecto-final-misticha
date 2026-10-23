package com.raymisupreme.msacopio.service.impl;

import com.raymisupreme.msacopio.dto.ArtesanoRequest;
import com.raymisupreme.msacopio.dto.ArtesanoResponse;
import com.raymisupreme.msacopio.entity.ArtesanoEntity;
import com.raymisupreme.msacopio.mapper.ArtesanoMapper;
import com.raymisupreme.msacopio.repository.ArtesanoRepository;
import com.raymisupreme.msacopio.service.ArtesanoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArtesanoServiceImpl implements ArtesanoService {

    @Autowired
    private ArtesanoRepository repository;

    @Autowired
    private ArtesanoMapper mapper;

    @Override
    public ArtesanoResponse guardar(ArtesanoRequest request) {
        ArtesanoEntity entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public List<ArtesanoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArtesanoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Artesano no encontrado con ID: " + id));
    }

    @Override
    public ArtesanoResponse actualizar(Long id, ArtesanoRequest request) {
        ArtesanoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artesano no encontrado con ID: " + id));
        entity.setNombres(request.getNombres());
        entity.setApellidos(request.getApellidos());
        entity.setDni(request.getDni());
        entity.setComunidadOZona(request.getComunidadOZona());
        entity.setTelefono(request.getTelefono());
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        ArtesanoEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artesano no encontrado con ID: " + id));
        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar el artesano porque tiene lotes asociados", ex);
        }
    }
}
