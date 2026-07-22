package com.tnf.account_service.Dto.Response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Trimmed view returned after deposit/withdraw - just the identifier and new balance.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "BalanceResponse",
        description = "Trimmed view returned after a deposit or withdrawal - the account identifier "
                + "and its updated balance.")
public class BalanceResponse {

    @Schema(
            description = "Business identifier of the affected account.",
            example = "AC000001",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String accountNumber;

    @Schema(
            description = "Account balance after the operation was applied.",
            example = "150.00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal balance;
}
