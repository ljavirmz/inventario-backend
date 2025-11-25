package com.serviciosocial.inventario_backend.repository;

import com.serviciosocial.inventario_backend.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
}