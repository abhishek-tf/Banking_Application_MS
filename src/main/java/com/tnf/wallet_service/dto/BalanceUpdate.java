package com.tnf.wallet_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BalanceUpdate {
    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal balance;

}