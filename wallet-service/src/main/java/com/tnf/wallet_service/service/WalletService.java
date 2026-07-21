package com.tnf.wallet_service.service;

import com.tnf.wallet_service.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    WalletResponse createWallet(WalletRequest walletRequestDto);
    ResponseEntity<List<AccountResponse>> createTopUp(String id);
    WalletResponse updatingMoney(String accountId, String customerId, String walletProvider, BalanceUpdate update);
    WalletResponse takingFromWallet( String customerId,  String accountNumber,String walletProvider,WithdrawRequest request);
    WalletResponse payToMerchant(String walletId,String accountNumber,WithdrawRequest request);
    void logFailure(String message);
}
