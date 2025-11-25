package com.serviciosocial.inventario_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequest {
    private String usuario;
    private String nombre;
    private String contrasena;
    private Integer nivel; // 1 = Admin, 2 = Usuario
}