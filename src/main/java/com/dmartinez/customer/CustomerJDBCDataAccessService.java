package com.dmartinez.customer;

import com.dmartinez.exception.RequestValidationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                """;

        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);

        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?;
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;
        int result = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        System.out.println("jdbcTemplate.update = " + result);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer customersCount = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return customersCount != null && customersCount  > 0;
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer customersCount = jdbcTemplate.queryForObject(sql, Integer.class, id);

        return customersCount != null && customersCount  > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        if (updatedCustomer.getName() != null) {
            var sql = """
                    UPDATE customer
                    SET name = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sql, updatedCustomer.getName(), updatedCustomer.getId());
        }

        if (updatedCustomer.getEmail() != null) {
            var sql = """
                    UPDATE customer
                    SET email = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sql, updatedCustomer.getEmail(), updatedCustomer.getId());
        }

        if (updatedCustomer.getAge() != null) {
            var sql = """
                    UPDATE customer
                    SET age = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sql, updatedCustomer.getAge(), updatedCustomer.getId());
        }
    }
}
