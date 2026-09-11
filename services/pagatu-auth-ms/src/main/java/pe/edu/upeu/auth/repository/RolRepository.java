package pe.edu.upeu.auth.repository;

import pe.edu.upeu.auth.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    boolean existsByNombre(String value);
}
