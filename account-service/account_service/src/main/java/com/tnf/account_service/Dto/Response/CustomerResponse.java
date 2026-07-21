package com.tnf.account_service.Dto.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// We only need the id to confirm the customer exists; ignoreUnknown lets customer-service
// evolve its payload without breaking this client.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerResponse {

    private String customerId;
}