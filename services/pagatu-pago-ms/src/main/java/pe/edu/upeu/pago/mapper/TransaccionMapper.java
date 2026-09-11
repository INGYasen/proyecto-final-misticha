package pe.edu.upeu.pago.mapper;

import pe.edu.upeu.pago.dto.TransaccionRequest;
import pe.edu.upeu.pago.dto.TransaccionResponse;
import pe.edu.upeu.pago.entity.Transaccion;
import org.springframework.stereotype.Component;

@Component
public class TransaccionMapper {

    public Transaccion toEntity(TransaccionRequest request) {
        return Transaccion.builder()
                .pagoId(request.getPagoId())
                .referencia(request.getReferencia())
                .canal(request.getCanal())
                .estado(request.getEstado())
                .build();
    }

    public TransaccionResponse toResponse(Transaccion entity) {
        return TransaccionResponse.builder()
                .id(entity.getId())
                .pagoId(entity.getPagoId())
                .referencia(entity.getReferencia())
                .canal(entity.getCanal())
                .estado(entity.getEstado())
                .build();
    }
}
