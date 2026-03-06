package com.dmartinez.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {}
