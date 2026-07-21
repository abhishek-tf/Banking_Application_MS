package com.tnf.account_service.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tnf.account_service.Enum.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// All account types share one collection, discriminated by accountType - no
// per-type documents. id is Mongo's internal _id and is never exposed externally;
// accountNumber is the business identifier used across services.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "accounts")
public class BankAccount {

    @Id
    private String id;

    @Indexed(unique = true)
    private String accountNumber;

    // Indexed for lookups when listing a customer's accounts.
    @Indexed
    private String customerId;
    
    private AccountType accountType;
    
    private BigDecimal balance;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
