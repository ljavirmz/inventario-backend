package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.dto.MessageResponse;
import com.serviciosocial.inventario_backend.dto.UsuarioUpdateRequest;
import com.serviciosocial.inventario_backend.model.Usuario;
import com.serviciosocial.inventario_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UsuarioController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo admin puede ver todos los usuarios
        if (user.getNivel() != 1) {
            return ResponseEntity.status(403)
                .body(new MessageResponse("No tienes permisos"));
        }
        
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Ocultar contraseñas
        usuarios.forEach(u -> u.setContrasena(null));
        
        return ResponseEntity.ok(usuarios);
    }
    
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario userAuth = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (userAuth.getNivel() != 1) {
            return ResponseEntity.status(403)
                .body(new MessageResponse("No tienes permisos"));
        }
        
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(new MessageResponse("Usuario desactivado"));
    }
    
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarUsuario(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario userAuth = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (userAuth.getNivel() != 1) {
            return ResponseEntity.status(403)
                .body(new MessageResponse("No tienes permisos"));
        }
        
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(new MessageResponse("Usuario activado"));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario userAuth = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo admin puede editar usuarios
        if (userAuth.getNivel() != 1) {
            return ResponseEntity.status(403)
                .body(new MessageResponse("No tienes permisos"));
        }
        
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (request.getUsuario() != null && !request.getUsuario().isEmpty()) {
            // Verificar que el nuevo username no esté en uso
            if (usuarioRepository.existsByUsuario(request.getUsuario()) && 
                !usuario.getUsuario().equals(request.getUsuario())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("El nombre de usuario ya está en uso"));
            }
            usuario.setUsuario(request.getUsuario());
        }
        
        usuario.setNombre(request.getNombre());
        usuario.setNivel(request.getNivel());
        usuario.setActivo(request.getActivo());
        usuario.setPermisosAreas(request.getPermisosAreas());
        
        // Solo actualizar contraseña si viene en el request
        if (request.getContrasena() != null && !request.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }
        
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(new MessageResponse("Usuario actualizado exitosamente"));
    }
}