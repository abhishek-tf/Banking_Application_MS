package com.tnf.account_service.Dto.Request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Amount is intentionally left unvalidated here; the service layer enforces null/<=0
// so the failure surfaces as InvalidAmountException (400) rather than a bean-validation error.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "WithdrawRequest",
        description = "Payload for withdrawing funds from an account.")
public class WithdrawRequest {

    @Schema(
            description = "Amount to withdraw. Must be strictly greater than zero (a null or non-positive "
                    + "value is rejected with HTTP 400) and may not exceed the current balance (HTTP 422).",
            example = "50.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;
}
