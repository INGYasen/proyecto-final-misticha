package pe.edu.upeu.auth.repository;

import pe.edu.upeu.auth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByUsername(String value);
}
