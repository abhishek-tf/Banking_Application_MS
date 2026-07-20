package com.tnf.account_service.Service;

import java.math.BigDecimal;

import com.tnf.account_service.Dto.Request.CreateAccountRequest;
import com.tnf.account_service.Dto.Request.DepositRequest;
import com.tnf.account_service.Dto.Request.WithdrawRequest;
import com.tnf.account_service.Dto.Response.AccountResponse;
import com.tnf.account_service.Dto.Response.BalanceResponse;

public interface AccountService {

    // Customer existence is verified via customer-service before the account is created;
    // account number is generated internally and balance starts at zero.
    AccountResponse createAccount(CreateAccountRequest request);

    // Rejects amounts <= 0 (InvalidAmountException).
    BalanceResponse deposit(String accountNumber, DepositRequest request);

    // Rejects amounts <= 0 and any amount exceeding the current balance.
    BalanceResponse withdraw(String accountNumber, WithdrawRequest request);

    AccountResponse getAccount(String accountNumber);
}
