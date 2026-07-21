package com.tnf.account_service.Exception;

// Withdrawal exceeds available balance; mapped to 422 (a valid request that can't be fulfilled).
public class InsufficientBalanceException extends RuntimeException {
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
