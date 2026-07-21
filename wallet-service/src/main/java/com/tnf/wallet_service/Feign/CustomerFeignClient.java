package com.tnf.wallet_service.Feign;

import com.tnf.wallet_service.dto.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "customer-service")
public interface CustomerFeignClient {
    @GetMapping("/customers/{customerId}")
    CustomerResponseDTO getCustomer(@PathVariable String customerId);
}
