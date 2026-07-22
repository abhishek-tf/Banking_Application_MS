package com.tnf.account_service.Enum;

import io.swagger.v3.oas.annotations.media.Schema;

// Stored as a field on BankAccount rather than as separate document types.
@Schema(
        name = "AccountType",
        description = "Type of bank account.",
        example = "SAVINGS")
public enum AccountType {

    @Schema(description = "Savings account.")
    SAVINGS,

    @Schema(description = "Current (checking) account.")
    CURRENT
}
