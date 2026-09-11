package pe.edu.upeu.pago.repository;

import pe.edu.upeu.pago.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
}
