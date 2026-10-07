package com.pallet.ordermanagement.service;



import com.pallet.ordermanagement.dto.request.ProductRequest;
import com.pallet.ordermanagement.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse addProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();
}