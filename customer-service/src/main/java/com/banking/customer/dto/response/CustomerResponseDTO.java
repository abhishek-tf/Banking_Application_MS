package com.banking.customer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "CustomerResponse", description = "Customer representation returned by the service")
public class CustomerResponseDTO {

    @Schema(example = "CUST1001")
    private String customerId;

    @Schema(example = "Asha Rao")
    private String name;

    @Schema(example = "asha@example.com")
    private String email;

    @Schema(example = "9876543210")
    private String phoneNumber;
}
