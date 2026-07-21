package com.tnf.wallet_service.mapper;

import com.tnf.wallet_service.dto.WalletHistoryRequest;
import com.tnf.wallet_service.dto.WalletRequest;
import com.tnf.wallet_service.dto.WalletResponse;
import com.tnf.wallet_service.entities.Wallet;
import com.tnf.wallet_service.entities.WalletHistory;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface WalletHistoryMapper {
        WalletHistory toEntity(WalletHistoryRequest dto);
 }

