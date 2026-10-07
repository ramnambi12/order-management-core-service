package com.pallet.ordermanagement.controller;

import com.pallet.ordermanagement.dto.response.CustomerKPIResponse;
import com.pallet.ordermanagement.service.KPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kpis")
@RequiredArgsConstructor
public class KPIController {

    private final KPIService kpiService;

    @GetMapping("/top-customers")
    public ResponseEntity<List<CustomerKPIResponse>> getTopCustomersByOrderCount() {

        return ResponseEntity.ok(
                kpiService.getTopCustomersByOrderCount()
        );
    }

    @GetMapping("/customer-order-summary")
    public ResponseEntity<List<CustomerKPIResponse>> getCustomerOrderSummary() {

        return ResponseEntity.ok(
                kpiService.getCustomerOrderSummary()
        );
    }
}