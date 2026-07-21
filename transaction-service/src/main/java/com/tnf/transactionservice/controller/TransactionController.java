package com.tnf.transactionservice.controller;

import com.tnf.transactionservice.dto.TransactionRequest;
import com.tnf.transactionservice.dto.TransactionResponse;
import com.tnf.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfers")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransactionRequest transactionRequest) {
        TransactionResponse response = transactionService.transfer(transactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getHistory(@RequestParam String accountNumber) {
        List<TransactionResponse> history = transactionService.getHistory(accountNumber);
        return ResponseEntity.ok(history);
    }

}
