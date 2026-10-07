package com.pallet.ordermanagement.repository;

import com.pallet.ordermanagement.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}