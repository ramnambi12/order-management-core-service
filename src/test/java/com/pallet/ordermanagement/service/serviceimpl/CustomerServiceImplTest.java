package com.pallet.ordermanagement.service.serviceimpl;

import com.pallet.ordermanagement.dto.request.CustomerRequest;
import com.pallet.ordermanagement.dto.response.CustomerResponse;
import com.pallet.ordermanagement.entity.customer.Customer;
import com.pallet.ordermanagement.exception.CustomerAlreadyExistsException;
import com.pallet.ordermanagement.exception.CustomerNotFoundException;
import com.pallet.ordermanagement.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void shouldAddCustomerSuccessfully() {

        CustomerRequest request = new CustomerRequest();
        request.setName("Ramu");
        request.setEmail("ramu@example.com");
        request.setPhone("9000000001");

        Customer savedCustomer = Customer.builder()
                .id(100000L)
                .name("Ramu")
                .email("ramu@example.com")
                .phone("9000000001")
                .build();

        when(customerRepository.existsByEmail("ramu@example.com"))
                .thenReturn(false);

        when(customerRepository.existsByPhone("9000000001"))
                .thenReturn(false);

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        CustomerResponse response =
                customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals(100000L, response.getId());
        assertEquals("Ramu", response.getName());
        assertEquals("ramu@example.com", response.getEmail());
        assertEquals("9000000001", response.getPhone());

        verify(customerRepository).existsByEmail("ramu@example.com");
        verify(customerRepository).existsByPhone("9000000001");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        CustomerRequest request = new CustomerRequest();
        request.setName("Ramu");
        request.setEmail("ramu@example.com");
        request.setPhone("9000000001");

        when(customerRepository.existsByEmail("ramu@example.com"))
                .thenReturn(true);

        assertThrows(
                CustomerAlreadyExistsException.class,
                () -> customerService.createCustomer(request)
        );

        verify(customerRepository).existsByEmail("ramu@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenPhoneAlreadyExists() {

        CustomerRequest request = new CustomerRequest();
        request.setName("Ramu");
        request.setEmail("ramu@example.com");
        request.setPhone("9000000001");

        when(customerRepository.existsByEmail("ramu@example.com"))
                .thenReturn(false);

        when(customerRepository.existsByPhone("9000000001"))
                .thenReturn(true);

        assertThrows(
                CustomerAlreadyExistsException.class,
                () -> customerService.createCustomer(request)
        );

        verify(customerRepository).existsByEmail("ramu@example.com");
        verify(customerRepository).existsByPhone("9000000001");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {

        Customer existingCustomer = Customer.builder()
                .id(100000L)
                .name("Old Name")
                .email("old@example.com")
                .phone("9000000001")
                .build();

        CustomerRequest request = new CustomerRequest();
        request.setName("New Name");
        request.setEmail("new@example.com");
        request.setPhone("9000000002");

        when(customerRepository.findById(100000L))
                .thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByEmailAndIdNot(
                "new@example.com", 100000L))
                .thenReturn(false);

        when(customerRepository.existsByPhoneAndIdNot(
                "9000000002", 100000L))
                .thenReturn(false);

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response =
                customerService.updateCustomer(100000L, request);

        assertNotNull(response);
        assertEquals(100000L, response.getId());
        assertEquals("New Name", response.getName());
        assertEquals("new@example.com", response.getEmail());
        assertEquals("9000000002", response.getPhone());

        verify(customerRepository).findById(100000L);

        verify(customerRepository).existsByEmailAndIdNot(
                "new@example.com", 100000L);

        verify(customerRepository).existsByPhoneAndIdNot(
                "9000000002", 100000L);

        verify(customerRepository).save(existingCustomer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCustomer() {

        CustomerRequest request = new CustomerRequest();
        request.setName("Ramu");
        request.setEmail("ramu@example.com");
        request.setPhone("9000000001");

        when(customerRepository.findById(999999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(999999L, request)
        );

        verify(customerRepository).findById(999999L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldGetCustomerByIdSuccessfully() {

        Customer customer = Customer.builder()
                .id(100000L)
                .name("Ramu")
                .email("ramu@example.com")
                .phone("9000000001")
                .build();

        when(customerRepository.findById(100000L))
                .thenReturn(Optional.of(customer));

        CustomerResponse response =
                customerService.getCustomerById(100000L);

        assertNotNull(response);
        assertEquals(100000L, response.getId());
        assertEquals("Ramu", response.getName());
        assertEquals("ramu@example.com", response.getEmail());
        assertEquals("9000000001", response.getPhone());

        verify(customerRepository).findById(100000L);
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistingCustomer() {

        when(customerRepository.findById(999999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(999999L)
        );

        verify(customerRepository).findById(999999L);
    }

    @Test
    void shouldGetAllCustomersSuccessfully() {

        Customer customer1 = Customer.builder()
                .id(100000L)
                .name("Ramu")
                .email("ramu@example.com")
                .phone("9000000001")
                .build();

        Customer customer2 = Customer.builder()
                .id(100001L)
                .name("Arun")
                .email("arun@example.com")
                .phone("9000000002")
                .build();

        when(customerRepository.findAll())
                .thenReturn(List.of(customer1, customer2));

        List<CustomerResponse> responses =
                customerService.getAllCustomers();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(100000L, responses.get(0).getId());
        assertEquals("Ramu", responses.get(0).getName());

        assertEquals(100001L, responses.get(1).getId());
        assertEquals("Arun", responses.get(1).getName());

        verify(customerRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoCustomersExist() {

        when(customerRepository.findAll())
                .thenReturn(List.of());

        List<CustomerResponse> responses =
                customerService.getAllCustomers();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(customerRepository).findAll();
    }
}