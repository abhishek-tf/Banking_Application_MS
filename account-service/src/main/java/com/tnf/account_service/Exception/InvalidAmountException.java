package com.tnf.account_service.Exception;

// Null or non-positive transaction amount; mapped to 400.
public class InvalidAmountException extends RuntimeException {
    
    public InvalidAmountException(String message) {
        super(message);
    }
    
    public InvalidAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
