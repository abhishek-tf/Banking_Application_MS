package com.tnf.account_service.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tnf.account_service.Dto.Request.CreateAccountRequest;
import com.tnf.account_service.Dto.Request.DepositRequest;
import com.tnf.account_service.Dto.Request.WithdrawRequest;
import com.tnf.account_service.Dto.Response.AccountResponse;
import com.tnf.account_service.Dto.Response.BalanceResponse;
import com.tnf.account_service.Service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Thin REST layer: validation via @Valid, all business rules live in AccountService.
@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("POST /accounts - Creating account for customer: {}", request.getCustomerId());
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<BalanceResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {
        log.info("POST /accounts/{}/deposit - Amount: {}", accountNumber, request.getAmount());
        BalanceResponse response = accountService.deposit(accountNumber, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<BalanceResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody WithdrawRequest request) {
        log.info("POST /accounts/{}/withdraw - Amount: {}", accountNumber, request.getAmount());
        BalanceResponse response = accountService.withdraw(accountNumber, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        log.info("GET /accounts/{} - Fetching account details", accountNumber);
        AccountResponse response = accountService.getAccount(accountNumber);
        return ResponseEntity.ok(response);
    }
}
