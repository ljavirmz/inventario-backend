package com.serviciosocial.inventario_backend.service;

import com.serviciosocial.inventario_backend.config.JwtUtil;
import com.serviciosocial.inventario_backend.dto.LoginRequest;
import com.serviciosocial.inventario_backend.dto.LoginResponse;
import com.serviciosocial.inventario_backend.dto.RegistroRequest;
import com.serviciosocial.inventario_backend.model.Usuario;
import com.serviciosocial.inventario_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsuario(),
                loginRequest.getContrasena()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = jwtUtil.generarToken(loginRequest.getUsuario());
        
        Usuario usuario = usuarioRepository.findByUsuario(loginRequest.getUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return new LoginResponse(
            token,
            usuario.getIdUsuario(),
            usuario.getUsuario(),
            usuario.getNombre(),
            usuario.getNivel()
        );
    }
    
    public Usuario registrarUsuario(RegistroRequest registroRequest) {
        if (usuarioRepository.existsByUsuario(registroRequest.getUsuario())) {
            throw new RuntimeException("El usuario ya existe");
        }
        
        Usuario usuario = new Usuario();
        usuario.setUsuario(registroRequest.getUsuario());
        usuario.setNombre(registroRequest.getNombre());
        usuario.setContrasena(passwordEncoder.encode(registroRequest.getContrasena()));
        usuario.setNivel(registroRequest.getNivel());
        usuario.setActivo(true);
        
        return usuarioRepository.save(usuario);
    }
}