package com.pallet.ordermanagement.service.serviceimpl;

import com.pallet.ordermanagement.dto.response.CustomerKPIResponse;
import com.pallet.ordermanagement.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KPIServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private KPIServiceImpl kpiService;

    @Test
    void shouldGetTopCustomersByOrderCount() {

        CustomerKPIResponse customer1 =
                new CustomerKPIResponse(
                        100000L,
                        "Ramu",
                        10L
                );

        CustomerKPIResponse customer2 =
                new CustomerKPIResponse(
                        100001L,
                        "Arun",
                        8L
                );

        when(orderRepository.findTopCustomersByOrderCount(
                PageRequest.of(0, 5)))
                .thenReturn(List.of(customer1, customer2));

        List<CustomerKPIResponse> responses =
                kpiService.getTopCustomersByOrderCount();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(100000L, responses.get(0).getCustomerId());
        assertEquals("Ramu", responses.get(0).getCustomerName());
        assertEquals(10L, responses.get(0).getTotalOrders());

        assertEquals(100001L, responses.get(1).getCustomerId());
        assertEquals("Arun", responses.get(1).getCustomerName());
        assertEquals(8L, responses.get(1).getTotalOrders());

        verify(orderRepository)
                .findTopCustomersByOrderCount(PageRequest.of(0, 5));
    }

    @Test
    void shouldReturnEmptyListWhenNoTopCustomersExist() {

        when(orderRepository.findTopCustomersByOrderCount(
                PageRequest.of(0, 5)))
                .thenReturn(List.of());

        List<CustomerKPIResponse> responses =
                kpiService.getTopCustomersByOrderCount();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(orderRepository)
                .findTopCustomersByOrderCount(PageRequest.of(0, 5));
    }

    @Test
    void shouldGetCustomerOrderSummary() {

        CustomerKPIResponse customer1 =
                new CustomerKPIResponse(
                        100000L,
                        "Ramu",
                        10L
                );

        CustomerKPIResponse customer2 =
                new CustomerKPIResponse(
                        100001L,
                        "Arun",
                        5L
                );

        when(orderRepository.findCustomerOrderSummary())
                .thenReturn(List.of(customer1, customer2));

        List<CustomerKPIResponse> responses =
                kpiService.getCustomerOrderSummary();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(100000L, responses.get(0).getCustomerId());
        assertEquals("Ramu", responses.get(0).getCustomerName());
        assertEquals(10L, responses.get(0).getTotalOrders());

        assertEquals(100001L, responses.get(1).getCustomerId());
        assertEquals("Arun", responses.get(1).getCustomerName());
        assertEquals(5L, responses.get(1).getTotalOrders());

        verify(orderRepository).findCustomerOrderSummary();
    }

    @Test
    void shouldReturnEmptyListWhenCustomerOrderSummaryIsEmpty() {

        when(orderRepository.findCustomerOrderSummary())
                .thenReturn(List.of());

        List<CustomerKPIResponse> responses =
                kpiService.getCustomerOrderSummary();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(orderRepository).findCustomerOrderSummary();
    }
}