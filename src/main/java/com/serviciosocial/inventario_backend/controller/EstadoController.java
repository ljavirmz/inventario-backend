package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.model.Estado;
import com.serviciosocial.inventario_backend.service.EstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estados")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class EstadoController {
    
    @Autowired
    private EstadoService estadoService;
    
    @GetMapping
    public ResponseEntity<List<Estado>> obtenerTodos() {
        return ResponseEntity.ok(estadoService.obtenerTodos());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Estado>> obtenerActivos() {
        return ResponseEntity.ok(estadoService.obtenerActivos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Estado> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estadoService.obtenerPorId(id));
    }
    
    @PostMapping
    public ResponseEntity<Estado> crear(@RequestBody Estado estado) {
        return ResponseEntity.ok(estadoService.crear(estado));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Estado> actualizar(@PathVariable Long id, @RequestBody Estado estado) {
        return ResponseEntity.ok(estadoService.actualizar(id, estado));
    }
    
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        estadoService.desactivar(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        estadoService.activar(id);
        return ResponseEntity.ok().build();
    }
}