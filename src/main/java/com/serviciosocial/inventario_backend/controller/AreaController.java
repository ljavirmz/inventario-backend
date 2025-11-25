package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.model.Area;
import com.serviciosocial.inventario_backend.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/areas")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AreaController {
    
    @Autowired
    private AreaService areaService;
    
    @GetMapping
    public ResponseEntity<List<Area>> obtenerTodas() {
        return ResponseEntity.ok(areaService.obtenerTodas());
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<Area>> obtenerActivas() {
        return ResponseEntity.ok(areaService.obtenerActivas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Area> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.obtenerPorId(id));
    }
    
    @PostMapping
    public ResponseEntity<Area> crear(@RequestBody Area area) {
        return ResponseEntity.ok(areaService.crear(area));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Area> actualizar(@PathVariable Long id, @RequestBody Area area) {
        return ResponseEntity.ok(areaService.actualizar(id, area));
    }
    
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        areaService.desactivar(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        areaService.activar(id);
        return ResponseEntity.ok().build();
    }
}