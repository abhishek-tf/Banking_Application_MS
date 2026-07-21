package com.tnf.account_service.Dto.Response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Trimmed view returned after deposit/withdraw - just the identifier and new balance.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceResponse {
    
    private String accountNumber;
    
    private BigDecimal balance;
}
