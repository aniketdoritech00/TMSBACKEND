package com.doritech.tmsservice.service;

import java.util.List;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.ProductRequest;

public interface ProductService {

    ResponseEntity createProduct(List<ProductRequest> productRequest);

    ResponseEntity getProductById(Long id);

    ResponseEntity getAllProduct();

	ResponseEntity deleteProductDetails();
}