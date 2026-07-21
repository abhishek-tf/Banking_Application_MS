package com.tnf.wallet_service.Feign;

import com.tnf.wallet_service.dto.AccountResponse;
import com.tnf.wallet_service.dto.BalanceResponse;
import com.tnf.wallet_service.dto.DepositRequest;
import com.tnf.wallet_service.dto.WithdrawRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "account-service")
public interface AccountFeignClient {
    @GetMapping("/accounts/customer/{customerId}")
    ResponseEntity<List<AccountResponse>> getAccounts(
            @PathVariable String customerId);
    @GetMapping("/accounts/{accountNumber}")
    ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountNumber);

    @PostMapping("/accounts/{accountNumber}/withdraw")
    ResponseEntity<BalanceResponse> withDraw(
            @PathVariable String accountNumber,
            @RequestBody WithdrawRequest request);
    @PostMapping("/accounts/{accountNumber}/deposit")
    ResponseEntity<BalanceResponse> deposit(
            @PathVariable String accountNumber,
            @RequestBody DepositRequest request
    );
}

