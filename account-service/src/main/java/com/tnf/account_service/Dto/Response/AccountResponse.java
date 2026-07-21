package com.tnf.account_service.Dto.Response;

import java.math.BigDecimal;

import com.tnf.account_service.Enum.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Externally facing account view - exposes accountNumber, never the Mongo _id.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    
    private String accountNumber;
    
    private String customerId;
    
    private AccountType accountType;
    
    private BigDecimal balance;
}
