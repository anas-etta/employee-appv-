package com.example.batch.MinIO;

import com.example.batch.Service.BatchErrorLogService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class MinIOService {

    private final MinioClient minioClient;
    private final BatchErrorLogService errorLogService; // Ajout du service pour enregistrer les erreurs

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinIOService(@Value("${minio.url}") String minioUrl,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey,
                        BatchErrorLogService errorLogService) {  // Injection du service BatchErrorLogService
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
        this.errorLogService = errorLogService; // Initialisation du service d'erreur
    }

    public void uploadFile(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getName())
                            .stream(inputStream, file.length(), -1)
                            .contentType("application/json")
                            .build()
            );
            System.out.println("✅ Fichier uploadé sur MinIO : " + file.getName());
        } catch (MinioException e) {
            System.err.println("❌ MinIO Error : " + e.getMessage());
            // Enregistrement de l'erreur dans la base de données
            errorLogService.logError("MinIO Error: " + e.getMessage(), getStackTrace(e), "MinIO upload");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'upload : " + e.getMessage());
            // Enregistrement de l'erreur dans la base de données
            errorLogService.logError("Upload Error: " + e.getMessage(), getStackTrace(e), "MinIO upload");
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
