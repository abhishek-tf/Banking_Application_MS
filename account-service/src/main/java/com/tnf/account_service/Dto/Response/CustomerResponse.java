package com.tnf.account_service.Dto.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        name = "CustomerResponse",
        description = "Minimal projection of a customer as returned by the Customer Service. Used "
                + "internally to verify that a customer exists before an account is created or listed.")
public class CustomerResponse {

    @Schema(
            description = "Identifier of the customer confirmed to exist by the Customer Service.",
            example = "CUST1001")
    private String customerId;
}
