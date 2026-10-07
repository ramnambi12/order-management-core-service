package com.pallet.ordermanagement.service;

import com.pallet.ordermanagement.dto.request.CustomerRequest;
import com.pallet.ordermanagement.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    List<CustomerResponse> getAllCustomers();
}