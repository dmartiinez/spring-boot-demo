package com.dmartinez.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CustomerRowMapperTest {
    @Test
    void testMapRow() throws SQLException{
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.getLong("id")).thenReturn(1L);
        Mockito.when(resultSet.getString("name")).thenReturn("John Smith");
        Mockito.when(resultSet.getString("email")).thenReturn("smith@gmail.com");
        Mockito.when(resultSet.getInt("age")).thenReturn(30);

        //When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        Customer expected  = new Customer(1L, "John Smith", "smith@gmail.com", 30);

        assertThat(actual).isEqualTo(expected);
    }
}
