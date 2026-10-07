package com.pallet.ordermanagement.service.serviceimpl;

import com.pallet.ordermanagement.dto.request.OrderItemRequest;
import com.pallet.ordermanagement.dto.request.OrderRequest;
import com.pallet.ordermanagement.dto.response.OrderResponse;
import com.pallet.ordermanagement.entity.customer.Customer;
import com.pallet.ordermanagement.entity.order.Order;
import com.pallet.ordermanagement.entity.product.Product;
import com.pallet.ordermanagement.exception.CustomerNotFoundException;
import com.pallet.ordermanagement.exception.InsufficientStockException;
import com.pallet.ordermanagement.exception.OrderNotFoundException;
import com.pallet.ordermanagement.exception.ProductNotFoundException;
import com.pallet.ordermanagement.repository.CustomerRepository;
import com.pallet.ordermanagement.repository.OrderRepository;
import com.pallet.ordermanagement.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderSuccessfully() {

        // Customer exists
        Customer customer = Customer.builder()
                .id(100000L)
                .name("Test Customer")
                .email("test@example.com")
                .phone("9000000001")
                .build();

        when(customerRepository.findById(100000L))
                .thenReturn(Optional.of(customer));

        // Product exists
        Product product = Product.builder()
                .id(100000L)
                .name("Test Product")
                .price(new BigDecimal("999.99"))
                .stock(50)
                .build();

        when(productRepository.findById(100000L))
                .thenReturn(Optional.of(product));

        // Request
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(100000L);
        itemRequest.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderNumber("ORD-100000");
        orderRequest.setCustomerId(100000L);
        orderRequest.setCurrency("INR");
        orderRequest.setItems(List.of(itemRequest));

        // Mock saved order
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {

                    Order order = invocation.getArgument(0);

                    order.setId(100000L);

                    return order;
                });

        // Execute
        OrderResponse response = orderService.createOrder(orderRequest);

        // Verify response
        assertNotNull(response);
        assertEquals(100000L, response.getId());
        assertEquals("ORD-100000", response.getOrderNumber());
        assertEquals(100000L, response.getCustomerId());
        assertEquals(new BigDecimal("1999.98"), response.getTotalAmount());
        assertEquals("INR", response.getCurrency());

        // Verify stock deduction
        assertEquals(48, product.getStock());

        // Verify repository interactions
        verify(customerRepository).findById(100000L);
        verify(productRepository).findById(100000L);
        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {

        Customer customer = Customer.builder()
                .id(100000L)
                .name("Test Customer")
                .email("test@example.com")
                .phone("9000000001")
                .build();

        when(customerRepository.findById(100000L))
                .thenReturn(Optional.of(customer));

        Product product = Product.builder()
                .id(100000L)
                .name("Test Product")
                .price(new BigDecimal("999.99"))
                .stock(5)
                .build();

        when(productRepository.findById(100000L))
                .thenReturn(Optional.of(product));

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(100000L);
        itemRequest.setQuantity(10);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderNumber("ORD-100001");
        orderRequest.setCustomerId(100000L);
        orderRequest.setCurrency("INR");
        orderRequest.setItems(List.of(itemRequest));

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(orderRequest)
        );

        // Stock should remain unchanged
        assertEquals(5, product.getStock());

        // Order should NOT be saved
        verify(orderRepository, never()).save(any(Order.class));

        // Product stock should NOT be updated
        verify(productRepository, never()).save(product);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        Customer customer = Customer.builder()
                .id(100000L)
                .name("Test Customer")
                .email("test@example.com")
                .phone("9000000001")
                .build();

        when(customerRepository.findById(100000L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findById(999999L))
                .thenReturn(Optional.empty());

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(999999L);
        itemRequest.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderNumber("ORD-100002");
        orderRequest.setCustomerId(100000L);
        orderRequest.setCurrency("INR");
        orderRequest.setItems(List.of(itemRequest));

        assertThrows(
                ProductNotFoundException.class,
                () -> orderService.createOrder(orderRequest)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findById(999999L))
                .thenReturn(Optional.empty());

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(100000L);
        itemRequest.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderNumber("ORD-100003");
        orderRequest.setCustomerId(999999L);
        orderRequest.setCurrency("INR");
        orderRequest.setItems(List.of(itemRequest));

        assertThrows(
                CustomerNotFoundException.class,
                () -> orderService.createOrder(orderRequest)
        );

        // Product lookup should never happen
        verify(productRepository, never()).findById(anyLong());

        // Order should never be saved
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldCreateOrderWithMultipleProducts() {

        Customer customer = Customer.builder()
                .id(100000L)
                .name("Test Customer")
                .email("test@example.com")
                .phone("9000000001")
                .build();

        when(customerRepository.findById(100000L))
                .thenReturn(Optional.of(customer));

        Product product1 = Product.builder()
                .id(100000L)
                .name("Product One")
                .price(new BigDecimal("100.00"))
                .stock(20)
                .build();

        Product product2 = Product.builder()
                .id(100001L)
                .name("Product Two")
                .price(new BigDecimal("250.00"))
                .stock(10)
                .build();

        when(productRepository.findById(100000L))
                .thenReturn(Optional.of(product1));

        when(productRepository.findById(100001L))
                .thenReturn(Optional.of(product2));

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(100000L);
        item1.setQuantity(2);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setProductId(100001L);
        item2.setQuantity(3);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderNumber("ORD-100004");
        orderRequest.setCustomerId(100000L);
        orderRequest.setCurrency("INR");
        orderRequest.setItems(List.of(item1, item2));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(100000L);
                    return order;
                });

        OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);

        // 100 × 2 + 250 × 3 = 950
        assertEquals(new BigDecimal("950.00"), response.getTotalAmount());

        // Stock deduction
        assertEquals(18, product1.getStock());
        assertEquals(7, product2.getStock());

        // Two order items
        assertEquals(2, response.getItems().size());

        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() {

        Order order = Order.builder()
                .id(100000L)
                .orderNumber("ORD-100000")
                .customerId(100000L)
                .totalAmount(new BigDecimal("1999.98"))
                .currency("INR")
                .build();

        when(orderRepository.findById(100000L))
                .thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(100000L);

        assertNotNull(response);
        assertEquals(100000L, response.getId());
        assertEquals("ORD-100000", response.getOrderNumber());
        assertEquals(100000L, response.getCustomerId());
        assertEquals(new BigDecimal("1999.98"), response.getTotalAmount());
        assertEquals("INR", response.getCurrency());

        verify(orderRepository).findById(100000L);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(999999L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(999999L)
        );

        verify(orderRepository).findById(999999L);
    }

    @Test
    void shouldGetOrdersByCustomerIdSuccessfully() {

        Customer customer = Customer.builder()
                .id(100000L)
                .name("Test Customer")
                .email("test@example.com")
                .phone("9000000001")
                .build();

        when(customerRepository.findById(100000L))
                .thenReturn(Optional.of(customer));

        Order order1 = Order.builder()
                .id(100000L)
                .orderNumber("ORD-100000")
                .customerId(100000L)
                .totalAmount(new BigDecimal("1000.00"))
                .currency("INR")
                .build();

        Order order2 = Order.builder()
                .id(100001L)
                .orderNumber("ORD-100001")
                .customerId(100000L)
                .totalAmount(new BigDecimal("2000.00"))
                .currency("INR")
                .build();

        when(orderRepository.findByCustomerId(100000L))
                .thenReturn(List.of(order1, order2));

        List<OrderResponse> responses =
                orderService.getOrdersByCustomerId(100000L);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals("ORD-100000", responses.get(0).getOrderNumber());
        assertEquals("ORD-100001", responses.get(1).getOrderNumber());

        verify(customerRepository).findById(100000L);
        verify(orderRepository).findByCustomerId(100000L);
    }

    @Test
    void shouldThrowExceptionWhenGettingOrdersForNonExistingCustomer() {

        when(customerRepository.findById(999999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> orderService.getOrdersByCustomerId(999999L)
        );

        verify(orderRepository, never()).findByCustomerId(anyLong());
    }
}