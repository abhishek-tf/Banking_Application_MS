package com.banking.customer.controller;

import com.banking.customer.dto.request.CustomerRequestDTO;
import com.banking.customer.dto.response.CustomerResponseDTO;
import com.banking.customer.dto.response.ErrorResponseDTO;
import com.banking.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer identity management API")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Create a new customer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created",
                    content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate customerId",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Fetch a customer by customerId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable String customerId) {
        CustomerResponseDTO response = customerService.getCustomerByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }
}
