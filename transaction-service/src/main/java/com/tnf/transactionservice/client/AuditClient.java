package com.tnf.transactionservice.client;

import com.tnf.transactionservice.client.dto.AuditLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-service")
public interface AuditClient {

    @PostMapping("/audit/logs")
    void log(@RequestBody AuditLogRequest request);
}
