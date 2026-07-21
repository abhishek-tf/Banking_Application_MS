package com.tnf.wallet_service.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CustomerResponseDTO {
    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
}
