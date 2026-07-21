package com.tnf.account_service.Dto.Request;

import java.math.BigDecimal;

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
public class WithdrawRequest {

    private BigDecimal amount;
}
