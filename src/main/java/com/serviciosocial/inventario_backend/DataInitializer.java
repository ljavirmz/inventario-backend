package com.serviciosocial.inventario_backend;

import com.serviciosocial.inventario_backend.model.Usuario;
import com.serviciosocial.inventario_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Crear usuario admin solo si no existe ningún usuario
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsuario("admin");
            admin.setNombre("Administrador del Sistema");
            admin.setContrasena(passwordEncoder.encode("admin123"));
            admin.setNivel(1); // 1 = Admin
            admin.setActivo(true);
            
            usuarioRepository.save(admin);
            System.out.println(" Usuario administrador creado:");
            System.out.println("   Usuario: admin");
            System.out.println("   Contraseña: admin123");
        }
    }
}