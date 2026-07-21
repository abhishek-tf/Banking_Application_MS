package com.tnf.wallet_service.dto;

import com.tnf.wallet_service.enums.ScannerCategory;
import com.tnf.wallet_service.enums.WalletStatus;
import com.tnf.wallet_service.enums.WalletProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Data
public class WalletResponse {
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    private BigDecimal balance;
    private ScannerCategory scannerCategory;
    private WalletProvider provider;
}
