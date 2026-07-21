package com.tnf.wallet_service.dto;

import com.tnf.wallet_service.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String accountNumber;

    private String customerId;

    private AccountType accountType;

    private BigDecimal balance;
}
