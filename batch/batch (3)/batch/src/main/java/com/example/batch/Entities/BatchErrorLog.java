package com.example.batch.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_error_logs")  // Nom de la table en base de données
@JsonIgnoreProperties(ignoreUnknown = true)  // Ignore les champs JSON non reconnus
public class BatchErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incrémentation
    private Long id;

    @JsonProperty("error_message")
    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @JsonProperty("file_name")
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @JsonProperty("timestamp")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Constructeur par défaut (obligatoire pour JPA)
    public BatchErrorLog() {
        this.timestamp = LocalDateTime.now();  // Définit la valeur de `timestamp` lors de la création
    }

    // Constructeur avec paramètres
    public BatchErrorLog(String errorMessage, String fileName) {
        this.errorMessage = errorMessage;
        this.fileName = fileName;
        this.timestamp = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
