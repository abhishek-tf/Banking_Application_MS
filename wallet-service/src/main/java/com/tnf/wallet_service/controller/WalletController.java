package com.tnf.wallet_service.controller;


import com.tnf.wallet_service.dto.*;
import com.tnf.wallet_service.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    @PostMapping("/create")
    public WalletResponse createAccount(@RequestBody WalletRequest dto){
        try{
            //check the customerId with the customer Service
            return walletService.createWallet(dto);
        }
        catch (Exception e){
            walletService.logFailure(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/topup/{customerId}")
    public ResponseEntity<List<AccountResponse>> createTopUp(@PathVariable  String customerId){
        try
        {
            return walletService.createTopUp(customerId);
        } catch (RuntimeException e) {
            walletService.logFailure(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @PutMapping("/topup/update")
    public WalletResponse updateBalanceFromAccount(@RequestParam String customerId,@RequestParam String accountNumber ,@RequestParam String walletProvider,@RequestBody BalanceUpdate update){

        try{
            if (!walletProvider.equals("PHONEPE") && !walletProvider.equals("PAYTM")) {
                throw new RuntimeException("WalletProvider is not of the type PHONEPAY OR PATYM");
            }
            return walletService.updatingMoney(accountNumber,customerId,walletProvider,update);
        } catch (RuntimeException e) {
            walletService.logFailure(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @PutMapping("/topup/withdraw")
    public  WalletResponse takingFromWallet(@RequestParam String customerId, @RequestParam String accountNumber,@RequestParam String walletProvider,@RequestBody WithdrawRequest request)
    {
        try {
            if (!walletProvider.equals("PHONEPE") && !walletProvider.equals("PAYTM")) {
                throw new RuntimeException("WalletProvider is not of the type PHONEPAY OR PATYM");
            }

            return walletService.takingFromWallet(customerId, accountNumber, walletProvider, request);
        } catch (RuntimeException e) {
            walletService.logFailure(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //I designed wallet in a such a way where only where i can pay to
    @PostMapping("/pay")
    public WalletResponse payToMerchant(@RequestParam String walletId,@RequestParam String accountNumber,@RequestBody WithdrawRequest request){
        try{
            return walletService.payToMerchant(walletId,accountNumber,request);
        } catch (RuntimeException e) {
            walletService.logFailure(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //now if he selects one bank account i would be calling to same akshay service and i will update the balance of it

}
