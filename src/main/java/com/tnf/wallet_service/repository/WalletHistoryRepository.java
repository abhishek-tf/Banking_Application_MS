package com.tnf.wallet_service.repository;

import com.tnf.wallet_service.entities.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory,Long> {
}
