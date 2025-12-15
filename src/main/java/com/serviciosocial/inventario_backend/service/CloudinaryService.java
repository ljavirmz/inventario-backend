package com.serviciosocial.inventario_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }
    
    public String subirImagen(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
            ObjectUtils.asMap(
                "folder", "inventario-productos", // Carpeta en Cloudinary
                "resource_type", "auto"
            ));
        
        return uploadResult.get("secure_url").toString();
    }
    
    public void eliminarImagen(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
    
    // Método auxiliar para extraer publicId de una URL de Cloudinary
    public String extraerPublicId(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return null;
        }
        
        // URL ejemplo: https://res.cloudinary.com/dmycloud/image/upload/v1234/inventario-productos/abc123.jpg
        // Extraer: inventario-productos/abc123
        String[] parts = imageUrl.split("/upload/");
        if (parts.length < 2) return null;
        
        String afterUpload = parts[1];
        String[] pathParts = afterUpload.split("/");
        if (pathParts.length < 3) return null;
        
        // Construir publicId sin extensión
        String folder = pathParts[1];
        String filename = pathParts[2].replaceAll("\\.[^.]+$", ""); // Quitar extensión
        return folder + "/" + filename;
    }
}