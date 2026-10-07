package com.pallet.ordermanagement.repository;

import com.pallet.ordermanagement.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}