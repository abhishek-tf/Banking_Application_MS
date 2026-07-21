package com.tnf.account_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

@Configuration
public class FeignConfig {

    // FULL logs request/response bodies for every inter-service call - useful while
    // the customer-service contract is still stabilising.
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
