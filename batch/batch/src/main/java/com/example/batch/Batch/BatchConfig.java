package com.example.batch.Batch;

import com.example.batch.Entities.Employee;
import com.example.batch.Repository.EmployeeRepository;
import com.example.batch.Service.BatchErrorLogService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Paths;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final EmployeeRepository employeeRepository;
    private final BatchErrorLogService errorLogService;


    private static final String DIRECTORY_PATH = "C:\\Users\\anase\\Downloads\\batch (3)2\\batch (3)\\batch (3)\\batch (3)\\batch\\src\\dossier_ndjson";

    public BatchConfig(EntityManagerFactory entityManagerFactory, EmployeeRepository employeeRepository, BatchErrorLogService errorLogService) {
        this.entityManagerFactory = entityManagerFactory;
        this.employeeRepository = employeeRepository;
        this.errorLogService = errorLogService;
    }

    @Bean
    public Job importEmployeeJob(JobRepository jobRepository, Step processFilesStep, FileMoveListener fileMoveListener) {
        return new JobBuilder("importEmployeeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(fileMoveListener)
                .flow(processFilesStep)
                .end()
                .build();
    }

    @Bean
    public Step processFilesStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager) {
        return new StepBuilder("processFilesStep", jobRepository)
                .<Employee, Employee>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(compositeWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public NDJsonItemReader reader() {

        return new NDJsonItemReader(Paths.get(DIRECTORY_PATH).toString(), errorLogService);
    }

    @Bean
    public EmployeeProcessor processor() {
        return new EmployeeProcessor();
    }

    @Bean
    public CompositeItemWriter<Employee> compositeWriter() {
        CompositeItemWriter<Employee> writer = new CompositeItemWriter<>();
        JpaItemWriter<Employee> jpaWriter = new JpaItemWriter<>();
        jpaWriter.setEntityManagerFactory(entityManagerFactory);
        writer.setDelegates(List.of(jpaWriter));
        return writer;
    }
}
