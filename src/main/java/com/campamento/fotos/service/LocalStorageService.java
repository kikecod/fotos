package com.campamento.fotos.service;

import com.campamento.fotos.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Profile("dev")
public class LocalStorageService implements StorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public String store(MultipartFile file, String folder) {
        try {
            // Crear directorio si no existe
            Path targetDir = Paths.get(uploadDir, folder);
            Files.createDirectories(targetDir);

            // Generar nombre único para evitar colisiones
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + extension;

            // Copiar archivo
            Path targetPath = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Retornar URL accesible
            return baseUrl + "/uploads/" + folder + "/" + filename;

        } catch (IOException e) {
            throw ApiException.badRequest("Error al guardar el archivo: " + e.getMessage());
        }
    }

    @Override
    public void delete(String fileUrl) {
        try {
            // Extraer path relativo de la URL
            String relativePath = fileUrl.replace(baseUrl + "/uploads/", "");
            Path filePath = Paths.get(uploadDir, relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log pero no fallar
        }
    }
}
