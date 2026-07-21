package com.example.Audit_Service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Audit_Service.entity.AuditLog;

public interface AuditRepository extends MongoRepository<AuditLog, String> {

}