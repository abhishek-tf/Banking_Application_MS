package com.banking.customer.advice;

import com.banking.customer.dto.response.ErrorResponseDTO;
import com.banking.customer.exception.CustomerNotFoundException;
import com.banking.customer.exception.DuplicateCustomerException;
import com.banking.customer.exception.InvalidEmailException;
import com.banking.customer.exception.InvalidPhoneNumberException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidEmail(
            InvalidEmailException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "InvalidEmailException", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPhoneNumber(
            InvalidPhoneNumberException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "InvalidPhoneNumberException", ex.getMessage(), request);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomerNotFound(
            CustomerNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "CustomerNotFoundException", ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateCustomer(
            DuplicateCustomerException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "DuplicateCustomerException", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "ValidationException", message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError",
                ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponseDTO> build(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
