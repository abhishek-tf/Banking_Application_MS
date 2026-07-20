package com.tnf.account_service.Exception;

// Missing account or customer; mapped to 404 by GlobalExceptionHandler.
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
