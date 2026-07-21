package com.tnf.account_service.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.tnf.account_service.Enum.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {
    
    @NotBlank(message = "customerId is required")
    private String customerId;
    
    @NotNull(message = "accountType is required")
    private AccountType accountType;
}
