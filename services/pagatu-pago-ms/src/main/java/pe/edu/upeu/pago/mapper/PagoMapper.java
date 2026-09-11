package pe.edu.upeu.pago.mapper;

import pe.edu.upeu.pago.dto.PagoRequest;
import pe.edu.upeu.pago.dto.PagoResponse;
import pe.edu.upeu.pago.entity.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public Pago toEntity(PagoRequest request) {
        return Pago.builder()
                .codigo(request.getCodigo())
                .ordenId(request.getOrdenId())
                .monto(request.getMonto())
                .estado(request.getEstado())
                .metodo(request.getMetodo())
                .build();
    }

    public PagoResponse toResponse(Pago entity) {
        return PagoResponse.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .ordenId(entity.getOrdenId())
                .monto(entity.getMonto())
                .estado(entity.getEstado())
                .metodo(entity.getMetodo())
                .build();
    }
}
