package com.tnf.transactionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TransactionRequest {
    @NotBlank
    private String fromAccount;
    @NotBlank
    private String toAccount;
    @Positive
    private BigDecimal amount;
}
