package com.dmartinez.customer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    public final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new IllegalArgumentException("customer with id [%s] not found".formatted(id)));
    }
}
