package com.example.batch.Batch;

import com.example.batch.MinIO.MinIOService;
import com.example.batch.Service.BatchErrorLogService; // Ajout de l'import pour le service d'enregistrement des erreurs
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
public class FileMoveListener extends JobExecutionListenerSupport {

    private final NDJsonItemReader reader;
    private final MinIOService minioService;
    private final BatchErrorLogService errorLogService; // Ajout du service d'enregistrement des erreurs

    private static final String SOURCE_DIRECTORY = "C:/Users/anase/Downloads/batch (3)/batch (3)/batch/src/dossier_ndjson";
    private static final String PROCESSED_DIRECTORY = "C:/Users/anase/Downloads/batch (3)/batch (3)/batch/src/main/processed";
    private static final String ERROR_DIRECTORY = "C:/Users/anase/Downloads/batch (3)/batch (3)/batch/src/main/error";

    public FileMoveListener(NDJsonItemReader reader, MinIOService minioService, BatchErrorLogService errorLogService) {
        this.reader = reader;
        this.minioService = minioService;
        this.errorLogService = errorLogService; // Initialisation du service d'erreur
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (reader == null) return;

        List<File> filesToMove = reader.getNdjsonFiles();

        for (File file : filesToMove) {
            try {
                Path sourcePath = file.toPath();
                Files.createDirectories(Path.of(PROCESSED_DIRECTORY));
                Files.createDirectories(Path.of(ERROR_DIRECTORY));

                Path targetPath;
                if (jobExecution.getStatus() == BatchStatus.COMPLETED && reader.isAtLeastOneValidLine()) {
                    targetPath = Path.of(PROCESSED_DIRECTORY, file.getName());
                } else {
                    targetPath = Path.of(ERROR_DIRECTORY, file.getName());
                }

                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Fichier déplacé : " + sourcePath + " → " + targetPath);

                if (Files.exists(targetPath)) {
                    // Upload sur MinIO après le déplacement
                    minioService.uploadFile(targetPath.toFile());

                    // Suppression du fichier source sans message de log
                    Files.deleteIfExists(sourcePath);
                }

            } catch (Exception e) {
                // Enregistrement de l'erreur dans la base de données
                System.err.println("❌ Erreur lors du traitement du fichier " + file.getName() + " : " + e.getMessage());
                e.printStackTrace();
                errorLogService.logError("Erreur lors du traitement du fichier " + file.getName(), e.getMessage(), "FileMoveListener"); // Enregistrer l'erreur dans la BD
            }
        }
    }
}
