package com.banking.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ErrorResponse", description = "Standard error payload for every failed request")
public class ErrorResponseDTO {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(example = "2026-07-16T10:15:30")
    private LocalDateTime timestamp;

    @Schema(example = "400")
    private int status;

    @Schema(example = "InvalidEmailException")
    private String error;

    @Schema(example = "Email is invalid")
    private String message;

    @Schema(example = "/customers")
    private String path;
}
