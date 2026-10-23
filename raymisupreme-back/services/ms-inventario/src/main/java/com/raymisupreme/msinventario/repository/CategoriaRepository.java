package com.raymisupreme.msinventario.repository;

import com.raymisupreme.msinventario.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
}