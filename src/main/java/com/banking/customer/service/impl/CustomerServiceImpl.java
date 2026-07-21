package com.banking.customer.service.impl;

import com.banking.customer.dto.request.CustomerRequestDTO;
import com.banking.customer.dto.response.CustomerResponseDTO;
import com.banking.customer.entity.Customer;
import com.banking.customer.exception.CustomerNotFoundException;
import com.banking.customer.exception.DuplicateCustomerException;
import com.banking.customer.exception.InvalidEmailException;
import com.banking.customer.exception.InvalidPhoneNumberException;
import com.banking.customer.repository.CustomerRepository;
import com.banking.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        validateEmail(request.getEmail());
        validatePhoneNumber(request.getPhoneNumber());
        validateUniqueCustomerId(request.getCustomerId());

        Customer customer = Customer.builder()
                .customerId(request.getCustomerId())
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Created customer with customerId={}", saved.getCustomerId());
        return toResponse(saved);
    }

    @Override
    public CustomerResponseDTO getCustomerByCustomerId(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with customerId: " + customerId));
        return toResponse(customer);
    }

    private void validateUniqueCustomerId(String customerId) {
        if (customerRepository.existsByCustomerId(customerId)) {
            throw new DuplicateCustomerException(
                    "Customer already exists with customerId: " + customerId);
        }
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Email is invalid: " + email);
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidPhoneNumberException(
                    "Phone number must contain exactly 10 digits: " + phoneNumber);
        }
    }

    private CustomerResponseDTO toResponse(Customer customer) {
        return CustomerResponseDTO.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
