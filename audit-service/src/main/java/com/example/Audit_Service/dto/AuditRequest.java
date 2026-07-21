package com.example.Audit_Service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequest {

    @NotBlank
    private String level;

    @NotBlank
    private String source;

    @NotBlank
    private String message;
}
