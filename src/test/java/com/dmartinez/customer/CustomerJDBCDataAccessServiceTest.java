package com.dmartinez.customer;

import com.dmartinez.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService customerJDBCDataAccessService;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    @BeforeEach
    void setUp() {
        customerJDBCDataAccessService = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID(),
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);

        List<Customer> actual = customerJDBCDataAccessService.selectAllCustomers();

        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        Long id = -1L;

        var actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual.isEmpty());
    }

    @Test
    void existsCustomerWithEmail() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        boolean actual = customerJDBCDataAccessService.existsCustomerWithEmail(email);

        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithEmailReturnsFalseWhenEmailDoesNotExist() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();

        boolean actual = customerJDBCDataAccessService.existsCustomerWithEmail(email);

        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean actual = customerJDBCDataAccessService.existsCustomerWithId(id);

        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdReturnsFalseWhenIdDoesNotExist() {
        Long id = -1L;

        boolean actual = customerJDBCDataAccessService.existsCustomerWithId(id);

        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        customerJDBCDataAccessService.deleteCustomerById(id);

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isNotPresent();
    }

    @Test
    void willUpdateAllPropertiesCustomer() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer updatedCustomer = new Customer(
                id,
                "Daniela Martinez",
                "test@gmail.com",
                21
        );

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValue(updatedCustomer);
    }

    @Test
    void willUpdateCustomerName() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;
        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newName = "Ms.Daniela Martinez";
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName(newName);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(updatedCustomer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willUpdateCustomerEmail() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;

        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newEmail = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setEmail(newEmail);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(updatedCustomer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willUpdateCustomerAge() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;
        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newAge = 30;
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setAge(newAge);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(updatedCustomer.getAge());
        });
    }

    @Test
    void willNotUpdateCustomerWhenNothingToUpdate() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;

        Customer customer = new Customer(
                name,
                email,
                age
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        Long id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);

        customerJDBCDataAccessService.updateCustomer(updatedCustomer);

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }
}