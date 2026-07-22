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
        name = "DepositRequest",
        description = "Payload for depositing funds into an account.")
public class DepositRequest {

    @Schema(
            description = "Amount to deposit. Must be strictly greater than zero; a null or non-positive "
                    + "value is rejected with HTTP 400 (InvalidAmountException).",
            example = "150.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;
}
