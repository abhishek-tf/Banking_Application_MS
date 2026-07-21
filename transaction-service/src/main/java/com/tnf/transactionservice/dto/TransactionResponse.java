package com.tnf.transactionservice.dto;

import com.tnf.transactionservice.entity.StatusType;
import com.tnf.transactionservice.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TransactionResponse {

        private String transactionId;
        private String fromAccount;
        private String toAccount;
        private BigDecimal amount;
        private TransactionType type;
        private StatusType status;
        private LocalDateTime timestamp;
}
