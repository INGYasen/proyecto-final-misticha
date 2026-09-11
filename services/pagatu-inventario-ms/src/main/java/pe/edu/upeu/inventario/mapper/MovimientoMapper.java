package pe.edu.upeu.inventario.mapper;

import pe.edu.upeu.inventario.dto.MovimientoResponse;
import pe.edu.upeu.inventario.entity.Movimiento;
import org.springframework.stereotype.Component;

@Component
public class MovimientoMapper {

    public MovimientoResponse toResponse(Movimiento movimiento) {
        return MovimientoResponse.builder()
                .id(movimiento.getId())
                .stockId(movimiento.getStock().getId())
                .tipo(movimiento.getTipo())
                .cantidad(movimiento.getCantidad())
                .motivo(movimiento.getMotivo())
                .fecha(movimiento.getFecha())
                .build();
    }
}
