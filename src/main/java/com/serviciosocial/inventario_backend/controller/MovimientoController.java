package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.model.Movimiento;
import com.serviciosocial.inventario_backend.model.Usuario;
import com.serviciosocial.inventario_backend.repository.UsuarioRepository;
import com.serviciosocial.inventario_backend.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class MovimientoController {
    
    @Autowired
    private MovimientoService movimientoService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @GetMapping
    public ResponseEntity<List<Movimiento>> obtenerTodos() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Si es admin (nivel 1), ve todos los movimientos
        if (user.getNivel() == 1) {
            return ResponseEntity.ok(movimientoService.obtenerTodos());
        }
        
        // Si es usuario normal (nivel 2), solo ve sus propios movimientos
        return ResponseEntity.ok(movimientoService.filtrarPorUsuario(user.getIdUsuario()));
    }
    
    @GetMapping("/paginado")
    public ResponseEntity<Page<Movimiento>> obtenerTodosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending().and(Sort.by("hora").descending()));
        
        // Si es admin (nivel 1), ve todos los movimientos
        if (user.getNivel() == 1) {
            return ResponseEntity.ok(movimientoService.obtenerTodosPaginados(pageable));
        }
        
        // Si es usuario normal (nivel 2), solo ve sus propios movimientos
        return ResponseEntity.ok(movimientoService.obtenerTodosPaginadosPorUsuario(user.getIdUsuario(), pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Movimiento movimiento = movimientoService.obtenerPorId(id);
        
        // Si es usuario normal, verificar que el movimiento sea suyo
        if (user.getNivel() != 1 && !movimiento.getUsuario().getIdUsuario().equals(user.getIdUsuario())) {
            return ResponseEntity.status(403).body("No tienes permisos para ver este movimiento");
        }
        
        return ResponseEntity.ok(movimiento);
    }
    
    @GetMapping("/filtrar/usuario/{idUsuario}")
    public ResponseEntity<?> filtrarPorUsuario(@PathVariable Long idUsuario) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo admin puede ver movimientos de otros usuarios
        if (user.getNivel() != 1 && !user.getIdUsuario().equals(idUsuario)) {
            return ResponseEntity.status(403).body("No tienes permisos para ver movimientos de otros usuarios");
        }
        
        return ResponseEntity.ok(movimientoService.filtrarPorUsuario(idUsuario));
    }
    
    @GetMapping("/filtrar/fecha/{fecha}")
    public ResponseEntity<List<Movimiento>> filtrarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<Movimiento> movimientos = movimientoService.filtrarPorFecha(fecha);
        
        // Si es usuario normal, filtrar solo sus movimientos
        if (user.getNivel() != 1) {
            movimientos = movimientos.stream()
                .filter(m -> m.getUsuario().getIdUsuario().equals(user.getIdUsuario()))
                .toList();
        }
        
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/filtrar/tabla/{tabla}")
    public ResponseEntity<List<Movimiento>> filtrarPorTabla(@PathVariable String tabla) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<Movimiento> movimientos = movimientoService.filtrarPorTabla(tabla);
        
        // Si es usuario normal, filtrar solo sus movimientos
        if (user.getNivel() != 1) {
            movimientos = movimientos.stream()
                .filter(m -> m.getUsuario().getIdUsuario().equals(user.getIdUsuario()))
                .toList();
        }
        
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/filtrar/accion/{accion}")
    public ResponseEntity<List<Movimiento>> filtrarPorAccion(@PathVariable String accion) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<Movimiento> movimientos = movimientoService.filtrarPorAccion(accion);
        
        // Si es usuario normal, filtrar solo sus movimientos
        if (user.getNivel() != 1) {
            movimientos = movimientos.stream()
                .filter(m -> m.getUsuario().getIdUsuario().equals(user.getIdUsuario()))
                .toList();
        }
        
        return ResponseEntity.ok(movimientos);
    }
    
}