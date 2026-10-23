package com.raymisupreme.msventas.repository;

import com.raymisupreme.msventas.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByKeycloakUserId(String keycloakUserId);
    Optional<ClienteEntity> findByEmailIgnoreCase(String email);
}
