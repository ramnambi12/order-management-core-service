package com.pallet.ordermanagement.service.serviceimpl;

import com.pallet.ordermanagement.dto.request.OrderItemRequest;
import com.pallet.ordermanagement.dto.request.OrderRequest;
import com.pallet.ordermanagement.dto.response.OrderItemResponse;
import com.pallet.ordermanagement.dto.response.OrderResponse;
import com.pallet.ordermanagement.entity.order.Order;
import com.pallet.ordermanagement.entity.order.OrderItem;
import com.pallet.ordermanagement.entity.product.Product;
import com.pallet.ordermanagement.exception.CustomerNotFoundException;
import com.pallet.ordermanagement.exception.InsufficientStockException;
import com.pallet.ordermanagement.exception.OrderNotFoundException;
import com.pallet.ordermanagement.exception.ProductNotFoundException;
import com.pallet.ordermanagement.repository.CustomerRepository;
import com.pallet.ordermanagement.repository.OrderRepository;
import com.pallet.ordermanagement.repository.ProductRepository;
import com.pallet.ordermanagement.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {

        // Validate customer
        customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(orderRequest.getCustomerId()));

        Order order = Order.builder()
                .orderNumber(orderRequest.getOrderNumber())
                .customerId(orderRequest.getCustomerId())
                .currency(orderRequest.getCurrency())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {

            // Find product
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ProductNotFoundException(itemRequest.getProductId()));

            // Check stock
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            // Get price from database
            BigDecimal unitPrice = product.getPrice();

            // Calculate item total
            BigDecimal totalPrice = unitPrice.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            );

            OrderItem item = OrderItem.builder()
                    .productId(product.getId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .order(order)
                    .build();

            order.getItems().add(item);

            // Add to order total
            totalAmount = totalAmount.add(totalPrice);

            // Deduct stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        // Set calculated order total
        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return convertToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllWithItems()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private OrderResponse convertToResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }

    @Override
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

}