package com.pallet.ordermanagement.service.serviceimpl;

import com.pallet.ordermanagement.dto.request.ProductRequest;
import com.pallet.ordermanagement.dto.response.ProductResponse;
import com.pallet.ordermanagement.entity.product.Product;
import com.pallet.ordermanagement.exception.ProductNotFoundException;
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
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldAddProductSuccessfully() {

        ProductRequest request = new ProductRequest(
                "Laptop",
                new BigDecimal("75000.00"),
                20
        );

        Product savedProduct = Product.builder()
                .id(100000L)
                .name("Laptop")
                .price(new BigDecimal("75000.00"))
                .stock(20)
                .build();

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response = productService.addProduct(request);

        assertNotNull(response);
        assertEquals(100000L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(new BigDecimal("75000.00"), response.getPrice());
        assertEquals(20, response.getStock());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldUpdateProductSuccessfully() {

        Product existingProduct = Product.builder()
                .id(100000L)
                .name("Old Laptop")
                .price(new BigDecimal("70000.00"))
                .stock(10)
                .build();

        ProductRequest request = new ProductRequest(
                "New Laptop",
                new BigDecimal("80000.00"),
                25
        );

        when(productRepository.findById(100000L))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response =
                productService.updateProduct(100000L, request);

        assertNotNull(response);
        assertEquals(100000L, response.getId());
        assertEquals("New Laptop", response.getName());
        assertEquals(new BigDecimal("80000.00"), response.getPrice());
        assertEquals(25, response.getStock());

        verify(productRepository).findById(100000L);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {

        ProductRequest request = new ProductRequest(
                "Laptop",
                new BigDecimal("75000.00"),
                20
        );

        when(productRepository.findById(999999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(999999L, request)
        );

        verify(productRepository).findById(999999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldGetProductByIdSuccessfully() {

        Product product = Product.builder()
                .id(100000L)
                .name("Laptop")
                .price(new BigDecimal("75000.00"))
                .stock(20)
                .build();

        when(productRepository.findById(100000L))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productService.getProductById(100000L);

        assertNotNull(response);
        assertEquals(100000L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(new BigDecimal("75000.00"), response.getPrice());
        assertEquals(20, response.getStock());

        verify(productRepository).findById(100000L);
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistingProduct() {

        when(productRepository.findById(999999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999999L)
        );

        verify(productRepository).findById(999999L);
    }

    @Test
    void shouldGetAllProductsSuccessfully() {

        Product product1 = Product.builder()
                .id(100000L)
                .name("Laptop")
                .price(new BigDecimal("75000.00"))
                .stock(20)
                .build();

        Product product2 = Product.builder()
                .id(100001L)
                .name("Monitor")
                .price(new BigDecimal("25000.00"))
                .stock(15)
                .build();

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<ProductResponse> responses =
                productService.getAllProducts();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(100000L, responses.get(0).getId());
        assertEquals("Laptop", responses.get(0).getName());

        assertEquals(100001L, responses.get(1).getId());
        assertEquals("Monitor", responses.get(1).getName());

        verify(productRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsExist() {

        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductResponse> responses =
                productService.getAllProducts();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(productRepository).findAll();
    }
}