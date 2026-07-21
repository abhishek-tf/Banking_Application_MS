package com.banking.customer.service;

import com.banking.customer.dto.request.CustomerRequestDTO;
import com.banking.customer.dto.response.CustomerResponseDTO;
import com.banking.customer.entity.Customer;
import com.banking.customer.exception.CustomerNotFoundException;
import com.banking.customer.exception.DuplicateCustomerException;
import com.banking.customer.exception.InvalidEmailException;
import com.banking.customer.exception.InvalidPhoneNumberException;
import com.banking.customer.repository.CustomerRepository;
import com.banking.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequestDTO request;

    @BeforeEach
    void setUp() {
        request = CustomerRequestDTO.builder()
                .customerId("CUST1001")
                .name("Asha Rao")
                .email("asha@example.com")
                .phoneNumber("9876543210")
                .build();
    }

    @Test
    void createCustomer_shouldPersistAndReturnResponse() {
        Customer saved = Customer.builder()
                .id("64f0c0c0c0c0c0c0c0c0c0c0")
                .customerId("CUST1001")
                .name("Asha Rao")
                .email("asha@example.com")
                .phoneNumber("9876543210")
                .build();

        when(customerRepository.existsByCustomerId("CUST1001")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        CustomerResponseDTO response = customerService.createCustomer(request);

        assertThat(response.getCustomerId()).isEqualTo("CUST1001");
        assertThat(response.getName()).isEqualTo("Asha Rao");
        assertThat(response.getEmail()).isEqualTo("asha@example.com");
        assertThat(response.getPhoneNumber()).isEqualTo("9876543210");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_shouldThrowWhenCustomerIdDuplicate() {
        when(customerRepository.existsByCustomerId("CUST1001")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(DuplicateCustomerException.class)
                .hasMessageContaining("CUST1001");

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_shouldThrowWhenEmailInvalid() {
        request.setEmail("not-an-email");

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(InvalidEmailException.class);

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_shouldThrowWhenPhoneNumberInvalid() {
        request.setPhoneNumber("12345");

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(InvalidPhoneNumberException.class);

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerByCustomerId_shouldReturnCustomer() {
        Customer customer = Customer.builder()
                .id("64f0c0c0c0c0c0c0c0c0c0c0")
                .customerId("CUST1001")
                .name("Asha Rao")
                .email("asha@example.com")
                .phoneNumber("9876543210")
                .build();

        when(customerRepository.findByCustomerId("CUST1001")).thenReturn(Optional.of(customer));

        CustomerResponseDTO response = customerService.getCustomerByCustomerId("CUST1001");

        assertThat(response.getCustomerId()).isEqualTo("CUST1001");
        assertThat(response.getEmail()).isEqualTo("asha@example.com");
    }

    @Test
    void getCustomerByCustomerId_shouldThrowWhenNotFound() {
        when(customerRepository.findByCustomerId("CUST9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerByCustomerId("CUST9999"))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("CUST9999");
    }
}
