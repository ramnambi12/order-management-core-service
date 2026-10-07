package com.pallet.ordermanagement.service;

import com.pallet.ordermanagement.dto.response.CustomerKPIResponse;

import java.util.List;

public interface KPIService {

    List<CustomerKPIResponse> getTopCustomersByOrderCount();

    List<CustomerKPIResponse> getCustomerOrderSummary();
}