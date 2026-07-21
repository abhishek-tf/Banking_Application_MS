package com.tnf.wallet_service.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "wallet_id", nullable = false, unique = true)
    private String walletId;


    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @NotBlank(message = "wallet provider cannot be blank")
    @Column(name = "wallet_provider")
    private String walletProvider;

    @NotNull(message = "amount cannot be null")
    @DecimalMin(value = "0.00")
    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "scanner_category")
    private String scannerCategory;

    @NotBlank(message = "status cannot be empty")
    @Column(name = "status")
    private String status;

    @DecimalMin(value = "0.00")
    @Column(name = "daily_transfer_amount", precision = 19, scale = 2)
    private BigDecimal dailyTransferAmount;

    @Column(name = "daily_transfer_date")
    private LocalDate dailyTransferDate;

    @NotNull(message = "CreatedAt cannot be empty")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Wallet() {
    }

    // Getters and setters

}