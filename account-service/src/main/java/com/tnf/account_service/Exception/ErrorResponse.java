package com.tnf.account_service.Exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Uniform error body returned by GlobalExceptionHandler for every failure.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "ErrorResponse",
        description = "Uniform error body returned for every failed request.")
public class ErrorResponse {

    @Schema(
            description = "Timestamp when the error was produced (ISO-8601 local date-time).",
            example = "2026-07-21T10:15:30.123")
    private String timestamp;

    @Schema(description = "HTTP status code.", example = "404")
    private int status;

    @Schema(
            description = "Short error identifier, typically the originating exception name.",
            example = "ResourceNotFoundException")
    private String error;

    @Schema(
            description = "Human-readable description of what went wrong.",
            example = "Account not found: AC000001")
    private String message;

    @Schema(
            description = "Request path that produced the error.",
            example = "/accounts/AC000001")
    private String path;
}
