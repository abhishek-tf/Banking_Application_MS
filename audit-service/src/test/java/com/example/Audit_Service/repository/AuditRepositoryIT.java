package com.example.Audit_Service.repository;

import com.example.Audit_Service.TestcontainersConfiguration;
import com.example.Audit_Service.entity.AuditLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestcontainersConfiguration.class)
class AuditRepositoryIT {

    @Autowired
    private AuditRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void save_assignsIdAndPersists() {
        AuditLog log = AuditLog.builder()
                .level("ERROR")
                .source("billing")
                .message("charge failed")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog saved = repository.save(log);

        assertThat(saved.getId()).isNotBlank();
        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void findAll_returnsAllSavedLogs() {
        repository.save(AuditLog.builder().level("INFO").source("a").message("m1").timestamp(LocalDateTime.now()).build());
        repository.save(AuditLog.builder().level("WARN").source("b").message("m2").timestamp(LocalDateTime.now()).build());

        assertThat(repository.findAll()).hasSize(2);
    }
}
