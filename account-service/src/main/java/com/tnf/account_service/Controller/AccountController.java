package com.tnf.account_service.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tnf.account_service.Dto.Request.CreateAccountRequest;
import com.tnf.account_service.Dto.Request.DepositRequest;
import com.tnf.account_service.Dto.Request.WithdrawRequest;
import com.tnf.account_service.Dto.Response.AccountResponse;
import com.tnf.account_service.Dto.Response.BalanceResponse;
import com.tnf.account_service.Exception.ErrorResponse;
import com.tnf.account_service.Service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Thin REST layer: validation via @Valid, all business rules live in AccountService.
@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Accounts", description = "Create bank accounts, deposit and withdraw funds, and query accounts.")
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Create a new account",
            description = """
                    Opens a new bank account for an existing customer.

                    Behaviour:
                    - The customer is validated through the Customer Service before anything is persisted.
                    - The account number is generated automatically (format AC000001).
                    - The initial balance is always 0.

                    Returns 404 if the customer does not exist. If the Customer Service cannot be reached
                    the request fails with 500 (the service maps the downstream outage to its generic
                    server-error body).""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    name = "Created",
                                    value = """
                                            {
                                              "accountNumber": "AC000001",
                                              "customerId": "CUST1001",
                                              "accountType": "SAVINGS",
                                              "balance": 0
                                            }"""))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed (e.g. missing customerId or accountType).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ValidationError",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 400,
                                              "error": "ValidationException",
                                              "message": "customerId is required",
                                              "path": "/accounts"
                                            }"""))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer does not exist in the Customer Service.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "CustomerNotFound",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 404,
                                              "error": "ResourceNotFoundException",
                                              "message": "Customer not found: CUST1001",
                                              "path": "/accounts"
                                            }"""))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error, or the Customer Service was unavailable while "
                            + "validating the customer (CustomerServiceUnavailableException maps to 500).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "CustomerServiceUnavailable",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 500,
                                              "error": "CustomerServiceUnavailableException",
                                              "message": "Customer service is unavailable while validating customer: CUST1001",
                                              "path": "/accounts"
                                            }""")))
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("POST /accounts - Creating account for customer: {}", request.getCustomerId());
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Deposit funds",
            description = """
                    Adds funds to an existing account and returns the updated balance.

                    The amount must be strictly greater than zero.""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit applied; the updated balance is returned.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BalanceResponse.class),
                            examples = @ExampleObject(
                                    name = "UpdatedBalance",
                                    value = """
                                            {
                                              "accountNumber": "AC000001",
                                              "balance": 150.00
                                            }"""))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Amount is null or not greater than zero.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "InvalidAmount",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 400,
                                              "error": "InvalidAmountException",
                                              "message": "Deposit amount must be greater than zero",
                                              "path": "/accounts/AC000001/deposit"
                                            }"""))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "AccountNotFound",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 404,
                                              "error": "ResourceNotFoundException",
                                              "message": "Account not found: AC000001",
                                              "path": "/accounts/AC000001/deposit"
                                            }"""))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<BalanceResponse> deposit(
            @Parameter(
                    description = "Business identifier of the target account.",
                    example = "AC000001",
                    required = true)
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {
        log.info("POST /accounts/{}/deposit - Amount: {}", accountNumber, request.getAmount());
        BalanceResponse response = accountService.deposit(accountNumber, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Withdraw funds",
            description = """
                    Removes funds from an existing account and returns the updated balance.

                    Rules:
                    - The amount must be strictly greater than zero.
                    - The amount may not exceed the current balance (no overdraft).

                    An insufficient balance is rejected with HTTP 422 (Unprocessable Entity).""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal applied; the updated balance is returned.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BalanceResponse.class),
                            examples = @ExampleObject(
                                    name = "UpdatedBalance",
                                    value = """
                                            {
                                              "accountNumber": "AC000001",
                                              "balance": 100.00
                                            }"""))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Amount is null or not greater than zero.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "InvalidAmount",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 400,
                                              "error": "InvalidAmountException",
                                              "message": "Withdrawal amount must be greater than zero",
                                              "path": "/accounts/AC000001/withdraw"
                                            }"""))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "AccountNotFound",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 404,
                                              "error": "ResourceNotFoundException",
                                              "message": "Account not found: AC000001",
                                              "path": "/accounts/AC000001/withdraw"
                                            }"""))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Insufficient balance to cover the requested withdrawal.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "InsufficientBalance",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 422,
                                              "error": "InsufficientBalanceException",
                                              "message": "Insufficient balance. Available: 50.00",
                                              "path": "/accounts/AC000001/withdraw"
                                            }"""))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<BalanceResponse> withdraw(
            @Parameter(
                    description = "Business identifier of the target account.",
                    example = "AC000001",
                    required = true)
            @PathVariable String accountNumber,
            @Valid @RequestBody WithdrawRequest request) {
        log.info("POST /accounts/{}/withdraw - Amount: {}", accountNumber, request.getAmount());
        BalanceResponse response = accountService.withdraw(accountNumber, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get account details",
            description = "Returns the full details of a single account by its account number.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    name = "Account",
                                    value = """
                                            {
                                              "accountNumber": "AC000001",
                                              "customerId": "CUST1001",
                                              "accountType": "SAVINGS",
                                              "balance": 150.00
                                            }"""))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "AccountNotFound",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 404,
                                              "error": "ResourceNotFoundException",
                                              "message": "Account not found: AC000001",
                                              "path": "/accounts/AC000001"
                                            }"""))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(
                    description = "Business identifier of the account to fetch.",
                    example = "AC000001",
                    required = true)
            @PathVariable String accountNumber) {
        log.info("GET /accounts/{} - Fetching account details", accountNumber);
        AccountResponse response = accountService.getAccount(accountNumber);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List a customer's accounts",
            description = """
                    Returns every account belonging to a customer.

                    Behaviour:
                    - The customer is validated through the Customer Service first.
                    - If the customer exists but owns no accounts, an empty list is returned (still 200).

                    Returns 404 if the customer does not exist. If the Customer Service cannot be reached
                    the request fails with 500 (the service maps the downstream outage to its generic
                    server-error body).""")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer validated; the (possibly empty) list of accounts is returned.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                                    schema = @Schema(implementation = AccountResponse.class)),
                            examples = {
                                    @ExampleObject(
                                            name = "AccountsFound",
                                            value = """
                                                    [
                                                      {
                                                        "accountNumber": "AC000001",
                                                        "customerId": "CUST1001",
                                                        "accountType": "SAVINGS",
                                                        "balance": 150.00
                                                      },
                                                      {
                                                        "accountNumber": "AC000002",
                                                        "customerId": "CUST1001",
                                                        "accountType": "CURRENT",
                                                        "balance": 0
                                                      }
                                                    ]"""),
                                    @ExampleObject(
                                            name = "NoAccounts",
                                            description = "Customer exists but owns no accounts.",
                                            value = "[]")
                            })),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer does not exist in the Customer Service.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "CustomerNotFound",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 404,
                                              "error": "ResourceNotFoundException",
                                              "message": "Customer not found: CUST1001",
                                              "path": "/accounts/customer/CUST1001"
                                            }"""))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error, or the Customer Service was unavailable while "
                            + "validating the customer (CustomerServiceUnavailableException maps to 500).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "CustomerServiceUnavailable",
                                    value = """
                                            {
                                              "timestamp": "2026-07-21T10:15:30.123",
                                              "status": 500,
                                              "error": "CustomerServiceUnavailableException",
                                              "message": "Customer service is unavailable while validating customer: CUST1001",
                                              "path": "/accounts/customer/CUST1001"
                                            }""")))
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(
            @Parameter(
                    description = "Identifier of the customer whose accounts are requested.",
                    example = "CUST1001",
                    required = true)
            @PathVariable String customerId) {
        log.info("GET /accounts/customer/{} - Fetching accounts for customer", customerId);
        List<AccountResponse> response = accountService.getAccountsByCustomer(customerId);
        return ResponseEntity.ok(response);
    }
}
