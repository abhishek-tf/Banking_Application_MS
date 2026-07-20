package com.tnf.account_service.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tnf.account_service.Dto.Response.CustomerResponse;

// Resolved by service name through Eureka - no hardcoded host/port.
// A 404 here means the customer doesn't exist and blocks account creation.
@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/customers/{customerId}")
    CustomerResponse getCustomer(@PathVariable("customerId") String customerId);

    // @GetMapping("/customers/bankaccount/{customerId}")
    // CustomerResponse getBankAccountByCustomerId(@PathVariable("customerId") String customerId);
}
