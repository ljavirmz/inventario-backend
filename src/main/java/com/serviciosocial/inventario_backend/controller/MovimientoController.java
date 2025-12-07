package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.model.Movimiento;
import com.serviciosocial.inventario_backend.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class MovimientoController {
    
    @Autowired
    private MovimientoService movimientoService;
    
    @GetMapping
    public ResponseEntity<List<Movimiento>> obtenerTodos() {
        return ResponseEntity.ok(movimientoService.obtenerTodos());
    }
    
    @GetMapping("/paginado")
    public ResponseEntity<Page<Movimiento>> obtenerTodosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending().and(Sort.by("hora").descending()));
        return ResponseEntity.ok(movimientoService.obtenerTodosPaginados(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }
    
    @GetMapping("/filtrar/usuario/{idUsuario}")
    public ResponseEntity<List<Movimiento>> filtrarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(movimientoService.filtrarPorUsuario(idUsuario));
    }
    
    @GetMapping("/filtrar/fecha/{fecha}")
    public ResponseEntity<List<Movimiento>> filtrarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(movimientoService.filtrarPorFecha(fecha));
    }
    
    @GetMapping("/filtrar/tabla/{tabla}")
    public ResponseEntity<List<Movimiento>> filtrarPorTabla(@PathVariable String tabla) {
        return ResponseEntity.ok(movimientoService.filtrarPorTabla(tabla));
    }
    
    @GetMapping("/filtrar/accion/{accion}")
    public ResponseEntity<List<Movimiento>> filtrarPorAccion(@PathVariable String accion) {
        return ResponseEntity.ok(movimientoService.filtrarPorAccion(accion));
    }
}