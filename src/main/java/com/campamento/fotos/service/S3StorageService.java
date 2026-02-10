package com.campamento.fotos.service;

import com.campamento.fotos.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@Profile("prod")
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.cloudfront.url}")
    private String cloudfrontUrl;

    public S3StorageService(@Value("${aws.region:us-east-1}") String region) {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .build(); // Usa credenciales de env vars: AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY
    }

    @Override
    public String store(MultipartFile file, String folder) {
        try {
            // Generar nombre único
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String key = folder + "/" + UUID.randomUUID() + extension;

            // Detectar content type
            String contentType = file.getContentType();
            if (contentType == null)
                contentType = "application/octet-stream";

            // Subir a S3
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Retornar URL de CloudFront
            return cloudfrontUrl + "/" + key;

        } catch (IOException e) {
            throw ApiException.badRequest("Error al subir archivo a S3: " + e.getMessage());
        }
    }

    @Override
    public void delete(String fileUrl) {
        try {
            // Extraer key de la URL de CloudFront
            String key = fileUrl.replace(cloudfrontUrl + "/", "");

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            // Log pero no fallar — el archivo puede ya no existir
        }
    }
}
