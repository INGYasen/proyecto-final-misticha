package com.raymisupreme.mscarrito.repository;

import com.raymisupreme.mscarrito.entity.CarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {

    Optional<CarritoEntity> findByClienteIdAndEstado(Long clienteId, String estado);
}
