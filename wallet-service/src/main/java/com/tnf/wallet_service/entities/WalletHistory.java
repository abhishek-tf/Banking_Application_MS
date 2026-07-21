package com.tnf.wallet_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "wallet_history")
public class WalletHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @NotBlank
    @Column(name = "transaction_type")
    private String transactionType;

    @NotNull(message = "amount cannot be null")
    @Positive
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "status cannot be null")
    @Column(name = "status")
    private String status;

    @Column(name = "upi_transaction_id")
    private String upiTransactionId;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "scanner_id")
    private String scannerId;

    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "failure_reason")
    private String failureReason;

    @NotNull(message = "created_At cannot be null")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WalletHistory() {
    }


}