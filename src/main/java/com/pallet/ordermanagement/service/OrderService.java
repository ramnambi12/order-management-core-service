package com.pallet.ordermanagement.service;

import com.pallet.ordermanagement.dto.request.OrderRequest;
import com.pallet.ordermanagement.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByCustomerId(Long customerId);
}