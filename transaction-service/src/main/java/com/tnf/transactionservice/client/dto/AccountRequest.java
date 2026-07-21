package com.tnf.transactionservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AccountRequest {
    private BigDecimal amount;
}
