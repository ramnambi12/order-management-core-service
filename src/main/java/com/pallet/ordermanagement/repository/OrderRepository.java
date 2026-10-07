package com.pallet.ordermanagement.repository;

import com.pallet.ordermanagement.dto.response.CustomerKPIResponse;
import com.pallet.ordermanagement.entity.order.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "items")
    List<Order> findByCustomerId(Long customerId);

    @EntityGraph(attributePaths = "items")
    @Query("SELECT o FROM Order o")
    List<Order> findAllWithItems();

    @EntityGraph(attributePaths = "items")
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    java.util.Optional<Order> findByIdWithItems(Long id);

    @Query("""
            SELECT new com.pallet.ordermanagement.dto.response.CustomerKPIResponse(
                o.customerId,
                c.name,
                COUNT(o)
            )
            FROM Order o
            JOIN Customer c ON c.id = o.customerId
            GROUP BY o.customerId, c.name
            ORDER BY COUNT(o) DESC
            """)
    List<CustomerKPIResponse> findTopCustomersByOrderCount(Pageable pageable);

    @Query("""
            SELECT new com.pallet.ordermanagement.dto.response.CustomerKPIResponse(
                o.customerId,
                c.name,
                COUNT(o)
            )
            FROM Order o
            JOIN Customer c ON c.id = o.customerId
            GROUP BY o.customerId, c.name
            ORDER BY c.name ASC
            """)
    List<CustomerKPIResponse> findCustomerOrderSummary();
}