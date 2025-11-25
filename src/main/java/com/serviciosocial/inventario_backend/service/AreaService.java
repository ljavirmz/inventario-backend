package com.serviciosocial.inventario_backend.service;

import com.serviciosocial.inventario_backend.model.Area;
import com.serviciosocial.inventario_backend.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AreaService {
    
    @Autowired
    private AreaRepository areaRepository;
    
    public List<Area> obtenerTodas() {
        return areaRepository.findAll();
    }
    
    public List<Area> obtenerActivas() {
        return areaRepository.findAll().stream()
            .filter(Area::getActivo)
            .toList();
    }
    
    public Area obtenerPorId(Long id) {
        return areaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Área no encontrada"));
    }
    
    public Area crear(Area area) {
        area.setActivo(true);
        return areaRepository.save(area);
    }
    
    public Area actualizar(Long id, Area areaActualizada) {
        Area area = obtenerPorId(id);
        area.setNombre(areaActualizada.getNombre());
        return areaRepository.save(area);
    }
    
    public void desactivar(Long id) {
        Area area = obtenerPorId(id);
        area.setActivo(false);
        areaRepository.save(area);
    }
    
    public void activar(Long id) {
        Area area = obtenerPorId(id);
        area.setActivo(true);
        areaRepository.save(area);
    }
}