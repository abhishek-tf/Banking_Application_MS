package com.banking.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customers")
public class Customer {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("customerId")
    private String customerId;

    @Field("name")
    private String name;

    @Field("email")
    private String email;

    @Field("phoneNumber")
    private String phoneNumber;
}
