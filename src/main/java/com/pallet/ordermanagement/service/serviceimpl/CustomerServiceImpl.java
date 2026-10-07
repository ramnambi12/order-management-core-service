package com.pallet.ordermanagement.service.serviceimpl;

import com.pallet.ordermanagement.dto.request.CustomerRequest;
import com.pallet.ordermanagement.dto.response.CustomerResponse;
import com.pallet.ordermanagement.entity.customer.Customer;
import com.pallet.ordermanagement.exception.CustomerAlreadyExistsException;
import com.pallet.ordermanagement.exception.CustomerNotFoundException;
import com.pallet.ordermanagement.repository.CustomerRepository;
import com.pallet.ordermanagement.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException(
                    "Customer with email '" + request.getEmail() + "' already exists"
            );
        }

        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new CustomerAlreadyExistsException(
                    "Customer with phone '" + request.getPhone() + "' already exists"
            );
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        return convertToResponse(savedCustomer);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (customerRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new CustomerAlreadyExistsException(
                    "Customer with email '" + request.getEmail() + "' already exists"
            );
        }

        if (customerRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new CustomerAlreadyExistsException(
                    "Customer with phone '" + request.getPhone() + "' already exists"
            );
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer updatedCustomer = customerRepository.save(customer);

        return convertToResponse(updatedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return convertToResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private CustomerResponse convertToResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}