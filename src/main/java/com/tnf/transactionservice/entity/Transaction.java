package com.tnf.transactionservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "transaction")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private TransactionType type;
    private StatusType status;
    private LocalDateTime timestamp;

}
