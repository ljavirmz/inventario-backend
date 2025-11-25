package com.serviciosocial.inventario_backend.service;

import com.serviciosocial.inventario_backend.model.Usuario;
import com.serviciosocial.inventario_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usuario));
        
        if (!user.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + usuario);
        }
        
        return new User(user.getUsuario(), user.getContrasena(), new ArrayList<>());
    }
}