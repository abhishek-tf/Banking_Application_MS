package com.tnf.account_service.Exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Uniform error body returned by GlobalExceptionHandler for every failure.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    
    private String timestamp;
    
    private int status;
    
    private String error;
    
    private String message;
    
    private String path;
}