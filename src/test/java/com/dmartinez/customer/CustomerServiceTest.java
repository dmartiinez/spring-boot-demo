package com.dmartinez.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dmartinez.exception.DuplicateResourceException;
import com.dmartinez.exception.RequestValidationException;
import com.dmartinez.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void testAddCustomer() {
        //Given
        String email = "john@gmail.com";

        Mockito.when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
            "John", email, 34
        );

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).insertCustomer(customArgumentCaptor.capture());
        Customer capturedCustomer = customArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo("John");
        assertThat(capturedCustomer.getEmail()).isEqualTo("john@gmail.com");
        assertThat(capturedCustomer.getAge()).isEqualTo(34);
    }

    @Test
    void testWillThrowWhenEmailExistsWhenAddingACustomer() {
        //Given
        String email = "john@gmail.com";

        Mockito.when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
            "John", email, 34
        );

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage("customer with email already exists");

        //Then
        Mockito.verify(customerDao, Mockito.never()).insertCustomer(Mockito.any());
    }

    @Test
    void testDeleteCustomerById() {
        //Given
         Long id = 2L;

        Mockito.when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        
        //When 
        underTest.deleteCustomerById(id);

        //Then
        Mockito.verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void testWillThrowWhenDeletingCustomerWithIdNotFound() {
        //Given
         Long id = 2L;

        Mockito.when(customerDao.existsCustomerWithId(id)).thenReturn(false);
        
        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("customer with id [%s] not found".formatted(id));

        //Then
        Mockito.verify(customerDao, Mockito.never()).deleteCustomerById(id);
    }

    @Test
    void testGetAllCustomers() {
        underTest.getAllCustomers();
        Mockito.verify(customerDao).selectAllCustomers();
    }

    @Test
    void testcanGetCustomerById() {
        Long id = 10L;
        Customer customer = new Customer(
            id, "John", "john@gmail.com", 34
        );
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Customer actual = underTest.getCustomerById(id);

        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void testwillThrowWhenGetCustomerByIdReturnsEmptyOptional() {
        Long id = 10L;
        
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerById(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("customer with id [%s] not found".formatted(id));

    }

    @Test
    void testcanUpdateAllCustomerProperties() {
        //Given
        String updateEmail = "ella@gmail.com";
        Long id = 10L;
        Customer customer = new Customer("Ella Doe", "ella-doe@gmail.com", 34);
        CustomerUpdateRequest update = new CustomerUpdateRequest("Ella Smith", updateEmail, 35);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDao.existsCustomerWithEmail(updateEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, update);

        //Then
        ArgumentCaptor<Customer> customArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customArgumentCaptor.capture());
        Customer capturedCustomer = customArgumentCaptor.getValue();


        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo("Ella Smith");
        assertThat(capturedCustomer.getEmail()).isEqualTo("ella@gmail.com");
        assertThat(capturedCustomer.getAge()).isEqualTo(35);
    }

    @Test
    void testwillThrowWhenUpdateCustomerAndNothingToUpdate() {
        //Given
        Long id = 10L;
        Customer customer = new Customer("Ella Doe", "ella-doe@gmail.com", 34);
        CustomerUpdateRequest update = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, update))
            .isInstanceOf(RequestValidationException.class)
            .hasMessage("no changes to customer found");

        Mockito.verify(customerDao, Mockito.never()).updateCustomer(Mockito.any());
    }

    @Test
    void testwillThrowWhenUpdateCustomerAndCustomerDoesNotExist() {
        //Given
        Long id = 10L;
        CustomerUpdateRequest update = new CustomerUpdateRequest("Ella Smith", "ella@gmail.com", 35);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, update))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("customer with id [%s] not found".formatted(id));
        
        Mockito.verify(customerDao, Mockito.never()).updateCustomer(Mockito.any());
    }

    @Test
    void testUpdateCustomerWithOnlyNameToUpdate() {
        //Given
        Customer customer = new Customer("Ella Doe", "ella-doe@gmail.com", 34);
        CustomerUpdateRequest update = new CustomerUpdateRequest("Ella Smith", null, null);
        Long id = 10L;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        underTest.updateCustomer(id, update);

        //Then
        ArgumentCaptor<Customer> customArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customArgumentCaptor.capture());
        Customer capturedCustomer = customArgumentCaptor.getValue();


        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo("Ella Smith");
        assertThat(capturedCustomer.getEmail()).isEqualTo("ella-doe@gmail.com");
        assertThat(capturedCustomer.getAge()).isEqualTo(34);
    }

    @Test
    void testUpdateCustomerWithOnlyEmailToUpdate() {
        //Given
        String updateEmail = "ella@gmail.com";
        Long id = 10L;
        Customer customer = new Customer("Ella Doe", "ella-doe@gmail.com", 34);
        CustomerUpdateRequest update = new CustomerUpdateRequest(null, updateEmail, null);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDao.existsCustomerWithEmail(updateEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, update);

        //Then
        ArgumentCaptor<Customer> customArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customArgumentCaptor.capture());
        Customer capturedCustomer = customArgumentCaptor.getValue();


        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo("Ella Doe");
        assertThat(capturedCustomer.getEmail()).isEqualTo("ella@gmail.com");
        assertThat(capturedCustomer.getAge()).isEqualTo(34);
    }

    @Test
    void testwillThrowWhenUpdateCustomerWithExistingEmail() {
        //Given
        String updateEmail = "ella@gmail.com";
        Long id = 10L;
        Customer customer = new Customer("Ella Doe", "ella-doe@gmail.com", 34);
        CustomerUpdateRequest update = new CustomerUpdateRequest("Ella Smith", updateEmail, null);    

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsCustomerWithEmail(updateEmail)).thenReturn(true);

        //Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, update))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage("customer with email already exists");

        Mockito.verify(customerDao, Mockito.never()).updateCustomer(Mockito.any());
    }

    @Test
    void testUpdateCustomerWithOnlyAgeToUpdate() {
        //Given
        Long id = 10L;
        Customer customer = new Customer("Ella Doe", "ella-doe@gmail.com", 34);
        CustomerUpdateRequest update = new CustomerUpdateRequest(null, null, 35);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        underTest.updateCustomer(id, update);

        //Then
        ArgumentCaptor<Customer> customArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customArgumentCaptor.capture());
        Customer capturedCustomer = customArgumentCaptor.getValue();


        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo("Ella Doe");
        assertThat(capturedCustomer.getEmail()).isEqualTo("ella-doe@gmail.com");
        assertThat(capturedCustomer.getAge()).isEqualTo(35);
    }
}
