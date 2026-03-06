package com.dmartinez.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {}
