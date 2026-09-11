package pe.edu.upeu.inventario.repository;

import pe.edu.upeu.inventario.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
}
