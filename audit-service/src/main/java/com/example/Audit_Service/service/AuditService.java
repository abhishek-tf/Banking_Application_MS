package com.example.Audit_Service.service;

import com.example.Audit_Service.dto.AuditRequest;
import com.example.Audit_Service.entity.AuditLog;

import java.util.List;

public interface AuditService {

    AuditLog saveLog(AuditRequest request);

    List<AuditLog> getLogs();

}
