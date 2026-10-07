package com.pallet.ordermanagement.service.serviceimpl;

import com.pallet.ordermanagement.dto.response.CustomerKPIResponse;
import com.pallet.ordermanagement.repository.OrderRepository;
import com.pallet.ordermanagement.service.KPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KPIServiceImpl implements KPIService {

    private final OrderRepository orderRepository;

    @Override
    public List<CustomerKPIResponse> getTopCustomersByOrderCount() {

        return orderRepository.findTopCustomersByOrderCount(
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<CustomerKPIResponse> getCustomerOrderSummary() {

        return orderRepository.findCustomerOrderSummary();
    }
}