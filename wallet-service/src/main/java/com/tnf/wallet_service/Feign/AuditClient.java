package com.tnf.wallet_service.Feign;

import com.tnf.wallet_service.dto.AuditLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-service")
public interface AuditClient {

    @PostMapping("/audit/logs")
    void log(@RequestBody AuditLogRequest request);
}
