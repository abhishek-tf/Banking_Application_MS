package com.banking.customer.repository;

import com.banking.customer.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByCustomerId(String customerId);

    boolean existsByCustomerId(String customerId);
}
