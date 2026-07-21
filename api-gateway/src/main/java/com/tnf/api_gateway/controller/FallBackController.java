package com.tnf.api_gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallBackController {

    private ResponseEntity<Map<String, String>> unavailable(String service) {
        Map<String, String> response = new HashMap<>();
        response.put("message", service + " is currently unavailable, please try again later");
        response.put("status", "503");
        return ResponseEntity.status(503).body(response);
    }

    @GetMapping("/customers")
    public ResponseEntity<Map<String, String>> customerServiceFallback() {
        return unavailable("Customer Service");
    }

    @GetMapping("/accounts")
    public ResponseEntity<Map<String, String>> accountServiceFallback() {
        return unavailable("Account Service");
    }

    @GetMapping("/transactions")
    public ResponseEntity<Map<String, String>> transactionServiceFallback() {
        return unavailable("Transaction Service");
    }

    @GetMapping("/audit")
    public ResponseEntity<Map<String, String>> auditServiceFallback() {
        return unavailable("Audit Service");
    }
}
