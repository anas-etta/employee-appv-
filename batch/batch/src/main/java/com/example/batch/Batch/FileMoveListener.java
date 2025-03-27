package com.example.batch.Batch;

import com.example.batch.Entities.Employee;
import com.example.batch.MinIO.MinIOService;
import com.example.batch.Service.BatchErrorLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
public class FileMoveListener extends JobExecutionListenerSupport {

    private final NDJsonItemReader reader;
    private final MinIOService minioService;
    private final BatchErrorLogService errorLogService;
    private final ObjectMapper objectMapper;

    private static final String SOURCE_DIRECTORY = "C:\\Users\\anase\\Downloads\\batch (3)2\\batch (3)\\batch (3)\\batch (3)\\batch\\src\\dossier_ndjson";
    private static final String PROCESSED_DIRECTORY = "C:\\Users\\anase\\Downloads\\batch (3)2\\batch (3)\\batch (3)\\batch (3)\\batch\\src\\main\\processed";
    private static final String ERROR_DIRECTORY = "C:\\Users\\anase\\Downloads\\batch (3)2\\batch (3)\\batch (3)\\batch (3)\\batch\\src\\main\\error";

    public FileMoveListener(NDJsonItemReader reader, MinIOService minioService, BatchErrorLogService errorLogService) {
        this.reader = reader;
        this.minioService = minioService;
        this.errorLogService = errorLogService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (reader == null) return;

        List<File> filesToMove = reader.getNdjsonFiles();


        for (File file : filesToMove) {
            boolean isValidFile = false;

            try {
                Path sourcePath = file.toPath();
                Files.createDirectories(Path.of(PROCESSED_DIRECTORY));
                Files.createDirectories(Path.of(ERROR_DIRECTORY));


                if (isFileValid(file)) {
                    isValidFile = true;
                }

                Path targetPath;
                if (isValidFile) {
                    targetPath = Path.of(PROCESSED_DIRECTORY, file.getName());
                } else {
                    targetPath = Path.of(ERROR_DIRECTORY, file.getName());
                }

                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Fichier déplacé : " + sourcePath + " → " + targetPath);

                if (Files.exists(targetPath)) {

                    minioService.uploadFile(targetPath.toFile());


                    Files.deleteIfExists(sourcePath);
                }

            } catch (Exception e) {

                System.err.println("Erreur lors du traitement du fichier " + file.getName() + " : " + e.getMessage());
                e.printStackTrace();
                errorLogService.logError("Erreur lors du traitement du fichier " + file.getName(), e.getMessage(), "FileMoveListener");
            }
        }
    }

    // Méthode pour valider un fichier individuellement
    private boolean isFileValid(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {

                    objectMapper.readValue(line, Employee.class);
                    return true;
                } catch (Exception e) {

                    continue;
                }
            }
        } catch (Exception e) {

            errorLogService.logError("Erreur lors de la lecture du fichier " + file.getName(), e.getMessage(), "FileMoveListener");
        }
        return false;
    }
}
