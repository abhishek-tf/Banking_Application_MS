package com.example.Audit_Service.controller;

import com.example.Audit_Service.dto.AuditRequest;
import com.example.Audit_Service.entity.AuditLog;
import com.example.Audit_Service.service.AuditService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService service;

    public AuditController(AuditService service) {
        this.service = service;
    }

    @PostMapping("/logs")
    public ResponseEntity<AuditLog> saveLog(@Valid @RequestBody AuditRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.saveLog(request));
    }

    @GetMapping("/logs")
    public List<AuditLog> getLogs() {
        return service.getLogs();
    }
}
