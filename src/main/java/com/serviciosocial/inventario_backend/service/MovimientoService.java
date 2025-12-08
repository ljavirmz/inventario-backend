package com.serviciosocial.inventario_backend.service;

import com.serviciosocial.inventario_backend.model.Movimiento;
import com.serviciosocial.inventario_backend.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MovimientoService {
    
    @Autowired
    private MovimientoRepository movimientoRepository;
    
    public List<Movimiento> obtenerTodos() {
        return movimientoRepository.findAll();
    }
    
    public Page<Movimiento> obtenerTodosPaginados(Pageable pageable) {
        return movimientoRepository.findAll(pageable);
    }
    
    public Movimiento obtenerPorId(Long id) {
        return movimientoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
    }
    
    // Filtros
    public List<Movimiento> filtrarPorUsuario(Long idUsuario) {
        return movimientoRepository.findAll().stream()
            .filter(m -> m.getUsuario().getIdUsuario().equals(idUsuario))
            .toList();
    }
    
    public List<Movimiento> filtrarPorFecha(LocalDate fecha) {
        return movimientoRepository.findAll().stream()
            .filter(m -> m.getFecha().equals(fecha))
            .toList();
    }
    
    public List<Movimiento> filtrarPorTabla(String tablaAfectada) {
        return movimientoRepository.findAll().stream()
            .filter(m -> m.getTablaAfectada().equalsIgnoreCase(tablaAfectada))
            .toList();
    }
    
    public List<Movimiento> filtrarPorAccion(String accion) {
        return movimientoRepository.findAll().stream()
            .filter(m -> m.getAccion().equalsIgnoreCase(accion))
            .toList();
    }
    
    public Page<Movimiento> obtenerTodosPaginadosPorUsuario(Long idUsuario, Pageable pageable) {
        // Implementación simple: obtener todos y filtrar
        // Para mejor rendimiento, deberías crear un método en el repository
        List<Movimiento> movimientos = movimientoRepository.findAll().stream()
            .filter(m -> m.getUsuario().getIdUsuario().equals(idUsuario))
            .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), movimientos.size());
        
        return new org.springframework.data.domain.PageImpl<>(
            movimientos.subList(start, end),
            pageable,
            movimientos.size()
        );
    }
}