package com.tnf.account_service.Dto.Response;

import java.math.BigDecimal;

import com.tnf.account_service.Enum.AccountType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Externally facing account view - exposes accountNumber, never the Mongo _id.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "AccountResponse",
        description = "Full account view returned when creating or fetching an account.")
public class AccountResponse {

    @Schema(
            description = "System-generated business identifier of the account (format AC + 6 digits).",
            example = "AC000001",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String accountNumber;

    @Schema(
            description = "Identifier of the customer who owns the account.",
            example = "CUST1001")
    private String customerId;

    @Schema(description = "Type of the account.", example = "SAVINGS")
    private AccountType accountType;

    @Schema(
            description = "Current account balance. A newly created account starts at 0.",
            example = "0")
    private BigDecimal balance;
}
