package com.tnf.wallet_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class CustomerResponseDTO {
    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
}
