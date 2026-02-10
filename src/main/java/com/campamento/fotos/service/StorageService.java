package com.campamento.fotos.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz de almacenamiento de archivos.
 * Implementación actual: LocalStorageService (disco local).
 * Futura implementación: S3StorageService (AWS S3).
 */
public interface StorageService {

    /**
     * Almacena un archivo y retorna su URL accesible.
     *
     * @param file   archivo a almacenar
     * @param folder subcarpeta (ej: "challenges/1")
     * @return URL pública del archivo
     */
    String store(MultipartFile file, String folder);

    /**
     * Elimina un archivo por su URL.
     */
    void delete(String fileUrl);
}
