package com.tnf.account_service.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Backing counter document for AccountNumberGenerator; one row per sequence id.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "sequences")
public class AccountSequence {

    @Id
    private String id;

    private Long sequence;
}