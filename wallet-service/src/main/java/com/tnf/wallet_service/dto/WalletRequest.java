package com.tnf.wallet_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WalletRequest {
    private String walletId;

    @NotBlank(message = "Customer ID cannot be blank")
    private String customerId;

    @NotBlank(message = "Wallet provider cannot be blank")
    private String walletProvider;

    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal balance;

    private String scannerCategory;

    private String status;

    @DecimalMin(value = "0.00", message = "Daily transfer amount cannot be negative")
    private BigDecimal dailyTransferAmount;

    private LocalDate dailyTransferDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
