package pe.edu.upeu.notificacion.service;

import pe.edu.upeu.notificacion.dto.AvisoRequest;
import pe.edu.upeu.notificacion.dto.AvisoResponse;
import pe.edu.upeu.notificacion.entity.Aviso;
import pe.edu.upeu.notificacion.exception.ResourceNotFoundException;
import pe.edu.upeu.notificacion.mapper.AvisoMapper;
import pe.edu.upeu.notificacion.repository.AvisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final AvisoMapper avisoMapper;

    public List<AvisoResponse> listar() {
        return avisoRepository.findAll().stream().map(avisoMapper::toResponse).toList();
    }

    public AvisoResponse obtener(Long id) {
        return avisoMapper.toResponse(buscarOFallar(id));
    }

    public AvisoResponse crear(AvisoRequest request) {
        return avisoMapper.toResponse(avisoRepository.save(avisoMapper.toEntity(request)));
    }

    public AvisoResponse actualizar(Long id, AvisoRequest request) {
        Aviso entity = buscarOFallar(id);
        entity.setCanalId(request.getCanalId());
        entity.setDestinatario(request.getDestinatario());
        entity.setAsunto(request.getAsunto());
        entity.setMensaje(request.getMensaje());
        entity.setEstado(request.getEstado());
        return avisoMapper.toResponse(avisoRepository.save(entity));
    }

    public void eliminar(Long id) {
        avisoRepository.delete(buscarOFallar(id));
    }

    private Aviso buscarOFallar(Long id) {
        return avisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aviso no encontrado: " + id));
    }
}
