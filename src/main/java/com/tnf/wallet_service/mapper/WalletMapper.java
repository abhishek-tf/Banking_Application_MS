package com.tnf.wallet_service.mapper;

import com.tnf.wallet_service.dto.WalletRequest;
import com.tnf.wallet_service.dto.WalletResponse;
import com.tnf.wallet_service.entities.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    Wallet toEntity(WalletRequest dto);

    WalletResponse toDto(Wallet wallet);
}