package com.serviciosocial.inventario_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {
    private String nombre;
    private String contrasena; // Opcional
    private Integer nivel;
    private Boolean activo;
    private String permisosAreas; // JSON string con IDs de áreas
}