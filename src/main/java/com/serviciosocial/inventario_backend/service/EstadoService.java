package com.serviciosocial.inventario_backend.service;

import com.serviciosocial.inventario_backend.model.Estado;
import com.serviciosocial.inventario_backend.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstadoService {
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    public List<Estado> obtenerTodos() {
        return estadoRepository.findAll();
    }
    
    public List<Estado> obtenerActivos() {
        return estadoRepository.findAll().stream()
            .filter(Estado::getActivo)
            .toList();
    }
    
    public Estado obtenerPorId(Long id) {
        return estadoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
    }
    
    public Estado crear(Estado estado) {
        estado.setActivo(true);
        return estadoRepository.save(estado);
    }
    
    public Estado actualizar(Long id, Estado estadoActualizado) {
        Estado estado = obtenerPorId(id);
        estado.setNombre(estadoActualizado.getNombre());
        return estadoRepository.save(estado);
    }
    
    public void desactivar(Long id) {
        Estado estado = obtenerPorId(id);
        estado.setActivo(false);
        estadoRepository.save(estado);
    }
    
    public void activar(Long id) {
        Estado estado = obtenerPorId(id);
        estado.setActivo(true);
        estadoRepository.save(estado);
    }
}