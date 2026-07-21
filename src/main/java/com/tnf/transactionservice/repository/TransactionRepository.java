package com.tnf.transactionservice.repository;

import com.tnf.transactionservice.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByFromAccountOrToAccount(String fromAccount, String toAccount);
}
