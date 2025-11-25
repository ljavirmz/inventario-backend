package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.dto.CambiarContrasenaRequest;
import com.serviciosocial.inventario_backend.dto.LoginRequest;
import com.serviciosocial.inventario_backend.dto.LoginResponse;
import com.serviciosocial.inventario_backend.dto.MessageResponse;
import com.serviciosocial.inventario_backend.dto.RegistroRequest;
import com.serviciosocial.inventario_backend.model.Usuario;
import com.serviciosocial.inventario_backend.repository.UsuarioRepository;
import com.serviciosocial.inventario_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Usuario o contraseña incorrectos"));
        }
    }
    
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest registroRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        // Buscar el usuario en BD para verificar su nivel
        Usuario usuarioAuth = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar que sea admin (nivel 1)
        if (usuarioAuth.getNivel() != 1) {
            return ResponseEntity.status(403)
                .body(new MessageResponse("No tienes permisos para crear usuarios"));
        }
        
        try {
            Usuario usuario = authService.registrarUsuario(registroRequest);
            return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuario = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // No devolver la contraseña
        user.setContrasena(null);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(@RequestBody CambiarContrasenaRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario usuario = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getContrasena())) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("La contraseña actual es incorrecta"));
        }
        
        // Actualizar con la nueva contraseña
        usuario.setContrasena(passwordEncoder.encode(request.getContrasenaNueva()));
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente"));
    }
}
