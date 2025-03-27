package com.example.batch.Service;

import com.example.batch.Entities.BatchErrorLog;
import com.example.batch.Repository.BatchErrorLogRepository;
import org.springframework.stereotype.Service;

@Service
public class BatchErrorLogService {

    private final BatchErrorLogRepository errorLogRepository;

    public BatchErrorLogService(BatchErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    public void logError(String errorMessage, String stackTrace, String fileName) {
        BatchErrorLog errorLog = new BatchErrorLog(errorMessage, fileName);
        errorLogRepository.save(errorLog);
    }
}
