package com.banking.customer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Schema(name = "CustomerRequest", description = "Payload to create a customer")
public class CustomerRequestDTO {

    @NotBlank(message = "customerId cannot be blank")
    @Schema(example = "CUST1001")
    private String customerId;

    @NotBlank(message = "name cannot be blank")
    @Schema(example = "Asha Rao")
    private String name;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "email must be a valid email address")
    @Schema(example = "asha@example.com")
    private String email;

    @NotBlank(message = "phoneNumber cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "phoneNumber must contain exactly 10 digits")
    @Schema(example = "9876543210")
    private String phoneNumber;
}
