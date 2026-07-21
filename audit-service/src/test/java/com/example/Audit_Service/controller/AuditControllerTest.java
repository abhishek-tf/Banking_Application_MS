package com.example.Audit_Service.controller;

import com.example.Audit_Service.dto.AuditRequest;
import com.example.Audit_Service.entity.AuditLog;
import com.example.Audit_Service.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuditService service;

    @Test
    void saveLog_returns201WithBody() throws Exception {
        AuditRequest request = new AuditRequest("INFO", "payment-service", "payment processed");
        AuditLog saved = AuditLog.builder()
                .id("1")
                .level("INFO")
                .source("payment-service")
                .message("payment processed")
                .timestamp(LocalDateTime.now())
                .build();
        when(service.saveLog(any(AuditRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/audit/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.level").value("INFO"))
                .andExpect(jsonPath("$.source").value("payment-service"));
    }

    @Test
    void saveLog_blankFieldsReturn400() throws Exception {
        AuditRequest invalid = new AuditRequest("", "", "");

        mockMvc.perform(post("/audit/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLogs_returns200WithList() throws Exception {
        AuditLog log = AuditLog.builder()
                .id("1").level("WARN").source("auth").message("failed login")
                .timestamp(LocalDateTime.now())
                .build();
        when(service.getLogs()).thenReturn(List.of(log));

        mockMvc.perform(get("/audit/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].level").value("WARN"))
                .andExpect(jsonPath("$[0].source").value("auth"));
    }
}
