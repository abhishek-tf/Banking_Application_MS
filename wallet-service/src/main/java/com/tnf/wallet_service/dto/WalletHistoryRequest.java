package com.tnf.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WalletHistoryRequest {
    private Long id;

    @NotBlank(message = "Wallet ID cannot be empty")
    private String walletId;

    @NotBlank(message = "Transaction type cannot be empty")
    private String transactionType;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Status cannot be empty")
    private String status;

    private String upiTransactionId;

    private String referenceNumber;

    private String accountNumber;

    private String merchantId;

    private String scannerId;

    private String source;

    private String destination;

    private String remarks;

    private String failureReason;

    private LocalDateTime createdAt;
}
