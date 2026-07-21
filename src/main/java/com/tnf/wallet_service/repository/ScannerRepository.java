package com.tnf.wallet_service.repository;

import com.tnf.wallet_service.entities.Scanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScannerRepository extends JpaRepository<Scanner,Long> {
    Optional<Scanner> findByBankAccount(String accountNumber);

}
