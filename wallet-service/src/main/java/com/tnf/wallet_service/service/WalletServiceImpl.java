package com.tnf.wallet_service.service;

import com.tnf.wallet_service.Feign.AccountFeignClient;
import com.tnf.wallet_service.Feign.CustomerFeignClient;
import com.tnf.wallet_service.dto.*;
import com.tnf.wallet_service.entities.Wallet;
import com.tnf.wallet_service.enums.AccountType;
import com.tnf.wallet_service.enums.ScannerCategory;
import com.tnf.wallet_service.mapper.WalletMapper;
import com.tnf.wallet_service.repository.ScannerRepository;
import com.tnf.wallet_service.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final AccountFeignClient accountFeignClient;
    private final ScannerRepository scannerRepository;
    private final CustomerFeignClient customerFeignClient;

    @Override
    public WalletResponse createWallet(WalletRequest dto) {
        //checking from the customer service
        CustomerResponseDTO c=customerFeignClient.getCustomer(dto.getCustomerId());
        if(c==null){
            throw new RuntimeException("Customer not found");
        }
        walletRepository.findAllByCustomerId(dto.getCustomerId())
                .ifPresent(wallets -> {
                    boolean exists = wallets.stream()
                            .anyMatch(wallet -> wallet.getWalletProvider()
                                    .equalsIgnoreCase(dto.getWalletProvider()));

                    if (exists) {
                        throw new RuntimeException(
                                "Customer already has a " + dto.getWalletProvider() + " wallet");
                    }
                });
        Wallet wallet = walletMapper.toEntity(dto);

        // Generate Wallet ID
        wallet.setWalletId("WLT-" + UUID.randomUUID().toString().substring(0, 8));

        // Default balance
            wallet.setBalance(BigDecimal.ZERO);
        if(wallet.getScannerCategory()==null){
            wallet.setScannerCategory(ScannerCategory.CUSTOMER.toString());
        }
        // Default status
        if (wallet.getStatus() == null || wallet.getStatus().isBlank()) {
            wallet.setStatus("ACTIVE");
        }

        // Daily transfer tracking
        wallet.setDailyTransferAmount(BigDecimal.ZERO);
        wallet.setDailyTransferDate(LocalDate.now());

        // Audit fields
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        wallet.setDeletedAt(null);

        return walletMapper.toDto(walletRepository.save(wallet));
    }

    @Override
    public ResponseEntity<List<AccountResponse>> createTopUp(String id) {
        List<AccountResponse> accountResponses = Optional
                .ofNullable(accountFeignClient.getAccounts(id).getBody())
                .orElseThrow(() -> new RuntimeException("No accounts found"));

               List<AccountResponse> savingsAccounts =   accountResponses.stream()
                       .filter(m->m.getAccountType().equals(AccountType.SAVINGS))
                       .toList();
               return ResponseEntity.ok(savingsAccounts);
    }

    @Override
    public WalletResponse updatingMoney(String accountId, String customerId, String walletProvider, BalanceUpdate update) {
       List<Wallet> wallets= walletRepository.findAllByCustomerId(customerId).orElseThrow(()->new RuntimeException("Customer not found with this Id"));
        Wallet wallet = wallets.stream()
                .filter(w -> w.getWalletProvider().equals(walletProvider))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Wallet provider not found"));
        AccountResponse accountResponse=accountFeignClient.getAccount(accountId).getBody();
        assert accountResponse != null;
        if(update.getBalance().compareTo(accountResponse.getBalance()) > 0){
            throw new RuntimeException("U dont have sufficient amount in Account to add to ur balance ");

        }
        //call the account service update the bank account balance
        //call here to update his service

//        depositRequest
        WithdrawRequest request = WithdrawRequest.builder()
                .amount(update.getBalance())
                .build();
        accountFeignClient.withDraw(accountId,request);
        wallet.setBalance(wallet.getBalance().add(update.getBalance()));
       return walletMapper.toDto( walletRepository.save(wallet) );

    }

    @Override
    public WalletResponse takingFromWallet(String customerId, String accountNumber,String walletProvider, WithdrawRequest request) {
        List<Wallet> wallets= walletRepository.findAllByCustomerId(customerId).orElseThrow(()->new RuntimeException("Customer not found with this Id"));
        Wallet wallet = wallets.stream()
                .filter(w -> w.getWalletProvider().equals(walletProvider))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Wallet provider not found"));

        if(wallet.getBalance().compareTo(request.getAmount()) > 0){
            throw new RuntimeException("U dont have sufficient amount in Account to add to ur balance ");

        }
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
      WalletResponse walletResponse= walletMapper.toDto( walletRepository.save(wallet) );
        DepositRequest depositRequest=DepositRequest.builder()
                .amount(request.getAmount())
                .build();
        accountFeignClient.deposit(accountNumber,depositRequest);
        return walletResponse;
    }

    @Override
    public WalletResponse payToMerchant(String walletId, String accountNumber,WithdrawRequest request) {
        scannerRepository.findByBankAccount(accountNumber).orElseThrow(()->new RuntimeException("Merchant Not Found"));
       Wallet wallet= walletRepository.findByWalletId(walletId).orElseThrow(()->new RuntimeException("wallet not found"));
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        DepositRequest depositRequest=DepositRequest.builder()
                .amount(request.getAmount())
                .build();
        accountFeignClient.deposit(accountNumber,depositRequest);
        return walletMapper.toDto(wallet);
    }


}
