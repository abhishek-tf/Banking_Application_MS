package com.banking.customer.service;

import com.banking.customer.dto.request.CustomerRequestDTO;
import com.banking.customer.dto.response.CustomerResponseDTO;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO request);

    CustomerResponseDTO getCustomerByCustomerId(String customerId);
}
