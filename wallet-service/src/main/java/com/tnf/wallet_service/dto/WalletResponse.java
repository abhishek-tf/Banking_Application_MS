package com.tnf.wallet_service.dto;

import com.tnf.wallet_service.enums.ScannerCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class WalletResponse {
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    private String walletId;
    private BigDecimal balance;
    private ScannerCategory scannerCategory;
    private String walletProvider;
}
