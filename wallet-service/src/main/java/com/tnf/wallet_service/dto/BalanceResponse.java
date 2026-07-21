package com.tnf.wallet_service.dto;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
public class BalanceResponse {
    private String accountNumber;
    private BigDecimal balance;
}
