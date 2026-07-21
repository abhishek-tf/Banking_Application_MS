package com.example.Audit_Service.service;

import com.example.Audit_Service.dto.AuditRequest;
import com.example.Audit_Service.entity.AuditLog;
import com.example.Audit_Service.repository.AuditRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditRepository repository;

    public AuditServiceImpl(AuditRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLog saveLog(AuditRequest request) {

        AuditLog log = AuditLog.builder()
                .level(request.getLevel())
                .source(request.getSource())
                .message(request.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return repository.save(log);
    }

    @Override
    public List<AuditLog> getLogs() {
        return repository.findAll();
    }

}
