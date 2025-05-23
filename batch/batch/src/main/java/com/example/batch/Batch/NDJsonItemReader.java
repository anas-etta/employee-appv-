package com.example.batch.Batch;

import com.example.batch.Entities.Employee;
import com.example.batch.Service.BatchErrorLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class NDJsonItemReader implements ItemReader<Employee> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BatchErrorLogService errorLogService;
    private Iterator<String> linesIterator;
    private boolean atLeastOneValidLine = false;
    private final List<File> ndjsonFiles;
    private String currentFileName = "";
    private boolean isFileValid = true;

    public NDJsonItemReader(String directoryPath, BatchErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
        try {
            Path absolutePath = Paths.get(directoryPath).toAbsolutePath();
            File directory = absolutePath.toFile();

            if (!directory.exists()) {
                throw new RuntimeException("Le dossier NDJSON n'existe pas : " + absolutePath);
            }
            if (!directory.isDirectory()) {
                throw new RuntimeException("Chemin invalide, ce n'est pas un dossier : " + absolutePath);
            }


            ndjsonFiles = Files.list(directory.toPath())
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(".ndjson"))
                    .collect(Collectors.toList());


            StringBuilder allLines = new StringBuilder();
            for (File file : ndjsonFiles) {
                currentFileName = file.getName();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    int lineNumber = 1;
                    while ((line = reader.readLine()) != null) {
                        allLines.append(line).append("\n");
                        lineNumber++;
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la lecture du fichier : " + currentFileName + " - " + e.getMessage());
                    errorLogService.logError("Erreur à la lecture du fichier : " + currentFileName + ", " + e.getMessage(),
                            getStackTrace(e), currentFileName);
                }
            }
            linesIterator = allLines.toString().lines().iterator();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture des fichiers NDJSON", e);
        }
    }

    @Override
    public Employee read() {
        int lineNumber = 1;
        while (linesIterator != null && linesIterator.hasNext()) {
            String line = linesIterator.next();
            try {
                Employee employee = objectMapper.readValue(line, Employee.class);
                atLeastOneValidLine = true;
                return employee;
            } catch (Exception e) {
                isFileValid = false;
                System.err.println("Ligne " + lineNumber + " ignorée en raison d'une erreur : " + e.getMessage());
                String errorMessage = "Erreur à la ligne " + lineNumber + " : " + e.getMessage();
                errorLogService.logError(errorMessage, getStackTrace(e), currentFileName);
            }
            lineNumber++;
        }
        return null;
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }


    public boolean isFileValid() {
        return isFileValid;
    }


    public boolean isAtLeastOneValidLine() {
        return atLeastOneValidLine;
    }

    public List<File> getNdjsonFiles() {
        return ndjsonFiles;
    }
}
