package com.tnf.account_service.Mapper;

import org.springframework.stereotype.Component;

import com.tnf.account_service.Dto.Response.AccountResponse;
import com.tnf.account_service.Dto.Response.BalanceResponse;
import com.tnf.account_service.Entity.BankAccount;

@Component
public class AccountMapper {

    // Responses are built field-by-field so id/createdAt/updatedAt never leak to callers.
    public AccountResponse toAccountResponse(BankAccount account) {
        return AccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomerId())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .build();
    }

    public BalanceResponse toBalanceResponse(BankAccount account) {
        return BalanceResponse.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }
    
}
