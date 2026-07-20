package com.tnf.account_service.Service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.tnf.account_service.Dto.Request.CreateAccountRequest;
import com.tnf.account_service.Dto.Request.DepositRequest;
import com.tnf.account_service.Dto.Request.WithdrawRequest;
import com.tnf.account_service.Dto.Response.AccountResponse;
import com.tnf.account_service.Dto.Response.BalanceResponse;
import com.tnf.account_service.Dto.Response.CustomerResponse;
import com.tnf.account_service.Entity.BankAccount;
import com.tnf.account_service.Exception.CustomerServiceUnavailableException;
import com.tnf.account_service.Exception.InsufficientBalanceException;
import com.tnf.account_service.Exception.InvalidAmountException;
import com.tnf.account_service.Exception.ResourceNotFoundException;
import com.tnf.account_service.Feign.CustomerClient;
import com.tnf.account_service.Mapper.AccountMapper;
import com.tnf.account_service.Repository.AccountRepository;
import com.tnf.account_service.Service.AccountService;
import com.tnf.account_service.Util.AccountNumberGenerator;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Business rules are enforced here rather than in the controller, so they hold
// regardless of how the service is invoked (REST, tests, future callers).
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CustomerClient customerClient;
    
    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for customer: {}", request.getCustomerId());

        // Ownership is verified against customer-service before an account can exist.
        // A 404 is a genuine "no such customer" (client error); any other Feign failure
        // means the dependency is down and must not be reported as a missing customer.
        try {
            CustomerResponse customerResponse = customerClient.getCustomer(request.getCustomerId());
            if (customerResponse == null || customerResponse.getCustomerId() == null) {
                throw new ResourceNotFoundException(
                        String.format("Customer not found: %s", request.getCustomerId()));
            }
            log.debug("Customer {} verified", request.getCustomerId());
        } catch (FeignException.NotFound e) {
            log.error("Customer not found: {}", request.getCustomerId());
            throw new ResourceNotFoundException(
                    String.format("Customer not found: %s", request.getCustomerId()));
        } catch (FeignException e) {
            log.error("Error validating customer: {}", request.getCustomerId(), e);
            throw new CustomerServiceUnavailableException(
                    String.format("Customer service is unavailable while validating customer: %s", request.getCustomerId()),
                    e);
        }

        String accountNumber = accountNumberGenerator.generateAccountNumber();
        log.debug("Generated account number: {}", accountNumber);

        BankAccount account = BankAccount.builder()
                .accountNumber(accountNumber)
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        account = accountRepository.save(account);
        log.info("Account created successfully: {}", accountNumber);

        return accountMapper.toAccountResponse(account);
    }
    
    @Override
    public BalanceResponse deposit(String accountNumber, DepositRequest request) {
        log.info("Deposit request for account: {} amount: {}", accountNumber, request.getAmount());

        // Amount is validated here (not via bean validation) so a non-positive value
        // surfaces as InvalidAmountException/400 per the API contract. BigDecimal.compareTo
        // avoids the scale pitfalls of equals (e.g. 0 vs 0.00).
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid deposit amount: {}", request.getAmount());
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }

        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountNumber);
                    return new ResourceNotFoundException(
                            String.format("Account not found: %s", accountNumber));
                });

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());

        account = accountRepository.save(account);
        log.info("Deposit successful. New balance: {} for account: {}", newBalance, accountNumber);

        return accountMapper.toBalanceResponse(account);
    }
    
    @Override
    public BalanceResponse withdraw(String accountNumber, WithdrawRequest request) {
        log.info("Withdrawal request for account: {} amount: {}", accountNumber, request.getAmount());

        // See deposit(): same rationale for service-level amount validation.
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid withdrawal amount: {}", request.getAmount());
            throw new InvalidAmountException("Withdrawal amount must be greater than zero");
        }

        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountNumber);
                    return new ResourceNotFoundException(
                            String.format("Account not found: %s", accountNumber));
                });

        // Overdrafts are not permitted; the balance must fully cover the withdrawal.
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            log.error("Insufficient balance. Current: {}, Requested: {}",
                    account.getBalance(), request.getAmount());
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance. Available: %s", account.getBalance()));
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());

        account = accountRepository.save(account);
        log.info("Withdrawal successful. New balance: {} for account: {}", newBalance, accountNumber);

        return accountMapper.toBalanceResponse(account);
    }
    
    @Override
    public AccountResponse getAccount(String accountNumber) {
        log.info("Fetching account: {}", accountNumber);
        
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountNumber);
                    return new ResourceNotFoundException(
                            String.format("Account not found: %s", accountNumber));
                });
        
        log.debug("Account found: {}", accountNumber);
        return accountMapper.toAccountResponse(account);
    }
}
