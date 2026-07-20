package com.tnf.account_service.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tnf.account_service.Entity.BankAccount;

// Lookups always go through accountNumber; the Mongo _id is never used as an external key.
@Repository
public interface AccountRepository extends MongoRepository<BankAccount, String> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    // Backed by the customerId index; returns empty when the customer owns no accounts.
    List<BankAccount> findByCustomerId(String customerId);
}
