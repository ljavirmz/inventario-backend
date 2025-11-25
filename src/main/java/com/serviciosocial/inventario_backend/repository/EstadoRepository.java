package com.serviciosocial.inventario_backend.repository;

import com.serviciosocial.inventario_backend.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
}