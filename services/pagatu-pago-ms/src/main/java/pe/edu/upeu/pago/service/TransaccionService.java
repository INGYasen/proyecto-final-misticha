package pe.edu.upeu.pago.service;

import pe.edu.upeu.pago.dto.TransaccionRequest;
import pe.edu.upeu.pago.dto.TransaccionResponse;
import pe.edu.upeu.pago.entity.Transaccion;
import pe.edu.upeu.pago.exception.ResourceNotFoundException;
import pe.edu.upeu.pago.mapper.TransaccionMapper;
import pe.edu.upeu.pago.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final TransaccionMapper transaccionMapper;

    public List<TransaccionResponse> listar() {
        return transaccionRepository.findAll().stream().map(transaccionMapper::toResponse).toList();
    }

    public TransaccionResponse obtener(Long id) {
        return transaccionMapper.toResponse(buscarOFallar(id));
    }

    public TransaccionResponse crear(TransaccionRequest request) {
        return transaccionMapper.toResponse(transaccionRepository.save(transaccionMapper.toEntity(request)));
    }

    public TransaccionResponse actualizar(Long id, TransaccionRequest request) {
        Transaccion entity = buscarOFallar(id);
        entity.setPagoId(request.getPagoId());
        entity.setReferencia(request.getReferencia());
        entity.setCanal(request.getCanal());
        entity.setEstado(request.getEstado());
        return transaccionMapper.toResponse(transaccionRepository.save(entity));
    }

    public void eliminar(Long id) {
        transaccionRepository.delete(buscarOFallar(id));
    }

    private Transaccion buscarOFallar(Long id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaccion no encontrado: " + id));
    }
}
