package com.raymisupreme.mscarrito.repository;

import com.raymisupreme.mscarrito.entity.ItemCarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarritoEntity, Long> {

    List<ItemCarritoEntity> findByCarrito_Id(Long carritoId);

    Optional<ItemCarritoEntity> findByIdAndCarrito_Id(Long id, Long carritoId);

    Optional<ItemCarritoEntity> findByCarrito_IdAndPrendaId(Long carritoId, Long prendaId);

    void deleteByCarrito_Id(Long carritoId);
}
