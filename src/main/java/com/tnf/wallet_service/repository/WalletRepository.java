package com.tnf.wallet_service.repository;

import com.tnf.wallet_service.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
//can you write an method where u actually find the customer using customer id and filter by the walletProvider
//Optional<Wallet> findByCustomerIdAndWalletProvider(
//        String customerId,
//        String walletProvider
//);
Optional<List<Wallet>>findAllByCustomerId(String customerId);
Optional<Wallet>findByWalletId(String walletId);

}
