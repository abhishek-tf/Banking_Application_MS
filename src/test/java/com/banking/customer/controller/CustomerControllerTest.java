package com.banking.customer.controller;

import com.banking.customer.dto.request.CustomerRequestDTO;
import com.banking.customer.dto.response.CustomerResponseDTO;
import com.banking.customer.exception.CustomerNotFoundException;
import com.banking.customer.exception.DuplicateCustomerException;
import com.banking.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void createCustomer_shouldReturn201() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .customerId("CUST1001")
                .name("Asha Rao")
                .email("asha@example.com")
                .phoneNumber("9876543210")
                .build();

        CustomerResponseDTO response = CustomerResponseDTO.builder()
                .customerId("CUST1001")
                .name("Asha Rao")
                .email("asha@example.com")
                .phoneNumber("9876543210")
                .build();

        when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("CUST1001"))
                .andExpect(jsonPath("$.email").value("asha@example.com"));
    }

    @Test
    void createCustomer_shouldReturn400WhenPhoneInvalid() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .customerId("CUST1001")
                .name("Asha Rao")
                .email("asha@example.com")
                .phoneNumber("123")
                .build();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/customers"));
    }

    @Test
    void createCustomer_shouldReturn409WhenDuplicate() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .customerId("CUST1001")
                .name("Asha Rao")
                .email("asha@example.com")
                .phoneNumber("9876543210")
                .build();

        when(customerService.createCustomer(any(CustomerRequestDTO.class)))
                .thenThrow(new DuplicateCustomerException("Customer already exists with customerId: CUST1001"));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DuplicateCustomerException"));
    }

    @Test
    void getCustomer_shouldReturn200() throws Exception {
        CustomerResponseDTO response = CustomerResponseDTO.builder()
                .customerId("CUST1001")
                .name("Asha Rao")
                .email("asha@example.com")
                .phoneNumber("9876543210")
                .build();

        when(customerService.getCustomerByCustomerId(eq("CUST1001"))).thenReturn(response);

        mockMvc.perform(get("/customers/{customerId}", "CUST1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST1001"))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"));
    }

    @Test
    void getCustomer_shouldReturn404WhenNotFound() throws Exception {
        when(customerService.getCustomerByCustomerId(eq("CUST9999")))
                .thenThrow(new CustomerNotFoundException("Customer not found with customerId: CUST9999"));

        mockMvc.perform(get("/customers/{customerId}", "CUST9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("CustomerNotFoundException"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
