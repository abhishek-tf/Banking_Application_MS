package com.tnf.account_service.Exception;

// Raised when the Feign call to customer-service fails for reasons other than a 404
// (timeout, connection refused, 5xx). Mapped to 500 - it's an upstream failure, not a client error.
public class CustomerServiceUnavailableException extends RuntimeException {

    public CustomerServiceUnavailableException(String message) {
        super(message);
    }

    public CustomerServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}