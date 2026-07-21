package com.tnf.transactionservice.service;

import com.tnf.transactionservice.client.AccountClient;
import com.tnf.transactionservice.client.AuditClient;
import com.tnf.transactionservice.client.dto.AccountRequest;
import com.tnf.transactionservice.client.dto.AuditLogRequest;
import com.tnf.transactionservice.dto.TransactionRequest;
import com.tnf.transactionservice.dto.TransactionResponse;
import com.tnf.transactionservice.entity.StatusType;
import com.tnf.transactionservice.entity.Transaction;
import com.tnf.transactionservice.entity.TransactionType;
import com.tnf.transactionservice.exception.InsufficientBalanceException;
import com.tnf.transactionservice.exception.InvalidAmountException;
import com.tnf.transactionservice.repository.TransactionRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private  TransactionRepository transactionRepository;
    @Autowired
    private AccountClient accountClient;
    @Autowired
    private AuditClient auditClient;


    public TransactionResponse transfer(TransactionRequest request) {
        validateRequest(request);
        AccountRequest payload = new AccountRequest(request.getAmount());

        try {
            accountClient.withdraw(request.getFromAccount(), payload);
        } catch (FeignException ex) {
            saveTransaction(request, StatusType.FAILED);
            logFailure("Withdraw failed during transfer: " + ex.getMessage());
            if (ex.status() == 422)
                throw new InsufficientBalanceException("Insufficient balance during transfer");
            throw ex;
        }
        try {
            accountClient.deposit(request.getToAccount(), payload);
        } catch (FeignException ex) {
            accountClient.deposit(request.getFromAccount(), payload);
            saveTransaction(request, StatusType.FAILED);
            logFailure("Deposit failed during transfer, sender refunded: " + ex.getMessage());
            if (ex.status() == 422)
                throw new InsufficientBalanceException("Insufficient balance during transfer");
            throw ex;
        }
        Transaction saved = saveTransaction(request, StatusType.SUCCESS);
        return toResponse(saved);
    }

    public List<TransactionResponse> getHistory(String accountNumber) {
        return transactionRepository
                .findByFromAccountOrToAccount(accountNumber, accountNumber)
                .stream()
                .map(this::toResponse)
                .toList();
    }



    private void validateRequest(TransactionRequest request) {
        if (request.getFromAccount() == null || request.getToAccount() == null)
            throw new IllegalArgumentException("Accounts must not be null");

        if (request.getFromAccount().equals(request.getToAccount()))
            throw new IllegalArgumentException("Sender and receiver cannot be same account");

        if (request.getAmount() == null || request.getAmount().signum() <= 0)
            throw new InvalidAmountException("Transfer amount must be positive");
    }

    private Transaction saveTransaction(TransactionRequest request, StatusType status) {
        Transaction txn = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(request.getFromAccount())
                .toAccount(request.getToAccount())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
        return transactionRepository.save(txn);
    }


    private void logFailure(String message) {
        try {
            auditClient.log(new AuditLogRequest("ERROR", "transaction-service", message));
        } catch (Exception ex) {
            System.err.println("[WARN] Could not reach audit-service: " + ex.getMessage());
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private TransactionResponse toResponse(Transaction txn) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(txn.getTransactionId());
        response.setFromAccount(txn.getFromAccount());
        response.setToAccount(txn.getToAccount());
        response.setAmount(txn.getAmount());
        response.setType(txn.getType());
        response.setStatus(txn.getStatus());
        response.setTimestamp(txn.getTimestamp());
        return response;
    }

}
