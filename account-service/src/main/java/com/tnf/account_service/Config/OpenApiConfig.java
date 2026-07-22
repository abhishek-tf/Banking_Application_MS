package com.tnf.account_service.Config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

// Central OpenAPI definition for the Account Service. Endpoint- and DTO-level detail
// lives on the annotations in the controller and DTOs; this bean supplies the document
// metadata (title, version, description, server) surfaced at the top of Swagger UI.
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI accountServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Account Service API")
                        .version("v1")
                        .description("""
                                REST API for the Banking Account Service.

                                Manages bank accounts and their balances. Customer ownership is verified
                                against the Customer Service (resolved via Eureka) before an account can be
                                created or listed, so responses can depend on that downstream service being
                                reachable.

                                Business rules:
                                - Account numbers are generated automatically (format AC000001).
                                - A new account always starts with a balance of 0.
                                - Deposits and withdrawals must be strictly greater than zero.
                                - Withdrawals may not exceed the current balance (no overdraft).""")
                        .contact(new Contact()
                                .name("Banking Microservices Team")
                                .email("Akshay.VAthreya@taylorandfrancis.com"))
                        .license(new License().name("Proprietary")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Local Account Service instance")));
    }
}
