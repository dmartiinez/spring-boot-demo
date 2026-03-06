package com.dmartinez.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CustomerJPADataAccessServiceTest {
    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    private Customer customer;

    @Mock
    private CustomerRepository customerRepository;
    
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
        customer = new Customer(
            "Jane Smith",
            "janesmith@gmail.com",
            20
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testDeleteCustomerById() {
        Long id = 1L;
        underTest.deleteCustomerById(id);
        Mockito.verify(customerRepository).deleteById(id);
    }

    @Test
    void testExistsCustomerWithEmail() {
        String email = "test@gmail.com";
        underTest.existsCustomerWithEmail(email);
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void testExistsCustomerWithId() {
        Long id = 2L;
        underTest.existsCustomerWithId(id);
        Mockito.verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void testInsertCustomer() {
        underTest.insertCustomer(customer);
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void testSelectAllCustomers() {
        underTest.selectAllCustomers();
        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void testSelectCustomerById() {
        Long id = 1L;

        underTest.selectCustomerById(id);
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void testUpdateCustomer() {
        underTest.updateCustomer(customer);
        Mockito.verify(customerRepository).save(customer);
    }
}
