package com.tnf.transactionservice.client;

import com.tnf.transactionservice.client.dto.AccountRequest;
import com.tnf.transactionservice.client.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PostMapping("/accounts/{accountNumber}/withdraw")
    AccountResponse withdraw(@PathVariable String accountNumber, @RequestBody AccountRequest request);

    @PostMapping("/accounts/{accountNumber}/deposit")
    AccountResponse deposit(@PathVariable String accountNumber, @RequestBody AccountRequest request);

}
