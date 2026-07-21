package com.example.Audit_Service.service;

import com.example.Audit_Service.dto.AuditRequest;
import com.example.Audit_Service.entity.AuditLog;
import com.example.Audit_Service.repository.AuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditRepository repository;

    @InjectMocks
    private AuditServiceImpl service;

    @Test
    void saveLog_mapsRequestFieldsAndSetsTimestamp() {
        AuditRequest request = new AuditRequest("INFO", "payment-service", "payment processed");
        // repository.save returns whatever it is given
        when(repository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime before = LocalDateTime.now();
        AuditLog result = service.saveLog(request);
        LocalDateTime after = LocalDateTime.now();

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        org.mockito.Mockito.verify(repository).save(captor.capture());
        AuditLog saved = captor.getValue();

        assertThat(saved.getLevel()).isEqualTo("INFO");
        assertThat(saved.getSource()).isEqualTo("payment-service");
        assertThat(saved.getMessage()).isEqualTo("payment processed");
        assertThat(saved.getTimestamp()).isBetween(before, after);
        assertThat(result).isSameAs(saved);
    }

    @Test
    void getLogs_returnsWhatRepositoryReturns() {
        AuditLog log = AuditLog.builder().level("WARN").source("auth").message("m").build();
        when(repository.findAll()).thenReturn(List.of(log));

        assertThat(service.getLogs()).containsExactly(log);
    }
}
