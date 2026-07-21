package com.tnf.wallet_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "scanner")
public class Scanner {

    @Id
    @NotBlank
    @Column(name = "scanner_id")
    private String scannerId;

    @NotBlank(message ="merchant name cannot be empty")
    @Column(name = "merchant_name")
    private String merchantName;

    @NotNull(message = "BankAccount cannot be null")
    private String bankAccount;

    @Column(name = "category")
    private String category;

    @NotBlank(message = "Status cannot be empty")
    @Column(name = "status")
    private String status;

    @NotNull(message = "CreatedAt cannot be null")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;



    public Scanner() {
    }
}